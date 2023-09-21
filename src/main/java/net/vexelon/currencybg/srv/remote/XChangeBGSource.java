package net.vexelon.currencybg.srv.remote;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.db.models.XChangeBGCurrencyPair;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XChangeBGSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(XChangeBGSource.class);
	private static final String TAG_NAME = XChangeBGSource.class.getSimpleName();

	private static final String URL_SOURCE  = "https://api.xchange.bg/api/exchange-pairs";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public XChangeBGSource(Vertx vertx, Reporter reporter) {
		super(vertx, reporter);
	}

	/**
	 * Transforms XchangeBG json data into {@link CurrencyData} models
	 */
	public List<CurrencyData> getXChangeRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		try (var reader = new InputStreamReader((input))) {
			var updateDate = DateTimeUtils.parseDate(LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), DATE_FORMAT);

			List<XChangeBGCurrencyPair> pairs = new GsonBuilder().create()
					.fromJson(reader, new TypeToken<List<XChangeBGCurrencyPair>>() {}.getType());

			for (var next : pairs) {
				var code = StringUtils.substringBefore(next.getSymbol(), "-");
				if (next.isActive() && Defs.CURRENCY_CODES_CRYPTO.contains(code) && next.getSymbol().endsWith("-BGN")) {
					try {
						var currencyData = new CurrencyData();
						currencyData.setCode(code);
						if (next.isEnableBuying()) {
							currencyData.setBuy(next.getBid());
						}
						if (next.isEnableSelling()) {
							currencyData.setSell(next.getAsk());
						}
						currencyData.setRatio(1);
						currencyData.setSource(Sources.XCHANGE.getID());
						currencyData.setDate(updateDate);
						result.add(currencyData);
					} catch (Exception e) {
						log.warn("Failed on {}, Exception={}", next.getSymbol(), e.getMessage());
						getReporter().write(TAG_NAME, "Could not process crypto {}", next.getSymbol());
					}
				} else if (log.isInfoEnabled()) {
					log.info("Skipped unsupported crypto: {}", next.getSymbol());
				}
			}

			return normalizeCurrencyData(result);
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void getRates(Callback callback) throws SourceException {
		doGet(URL_SOURCE, new HTTPCallback() {

			@Override
			public void onRequestFailed(Throwable t) {
				getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(t));

				XChangeBGSource.this.close();
				callback.onFailed(t);
			}

			@Override
			public void onRequestCompleted(HttpResponseWrapper response, boolean isCanceled) {
				var result = new ArrayList<CurrencyData>();

				if (!isCanceled) {
					try (var input = new ByteArrayInputStream(response.content())) {
						result.addAll(getXChangeRates(input));
					} catch (IOException | ParseException e) {
						log.error("Could not parse source data!", e);
						getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
					}
				} else {
					log.warn("Request was canceled! No currencies were downloaded.");
					getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
				}

				XChangeBGSource.this.close();
				callback.onCompleted(result);
			}
		});
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}
}
