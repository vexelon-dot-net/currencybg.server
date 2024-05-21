package net.vexelon.currencybg.srv.remote;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
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
import java.util.Map;

public class AltcoinsSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(AltcoinsSource.class);
	private static final String TAG_NAME = AltcoinsSource.class.getSimpleName();

	private static final String URL_SOURCE  = "https://altcoins.bg/currencies/rates";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public AltcoinsSource(Vertx vertx, Reporter reporter) {
		super(vertx, reporter);
	}

	/**
	 * Transforms Altcoins json data into {@link CurrencyData} models
	 */
	public List<CurrencyData> getAltcoinsRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		try (var reader = new InputStreamReader((input))) {
			var updateDate = DateTimeUtils.parseDate(LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), DATE_FORMAT);

			var gson = new GsonBuilder().create();
			var pairs = gson.fromJson(reader, Map.class);

			for (var entry : pairs.entrySet()) {
				var next = (Map.Entry<String, Object>) entry;

				if (Defs.CURRENCY_CODES_CRYPTO.contains(next.getKey()) && next.getValue() instanceof List valueList) {
					try {
						// parse value
						var currencyData = new CurrencyData();
						currencyData.setCode(next.getKey());

						if (valueList.get(1) instanceof Double d) {
							currencyData.setBuy(Double.toString(d));
						} else if (valueList.get(1) instanceof String str) {
							currencyData.setBuy(str);
						} else {
							throw new RuntimeException("Unsupported value type: " + valueList.get(1));
						}

						if (valueList.get(0) instanceof Double d) {
							currencyData.setSell(Double.toString(d));
						} else if (valueList.get(0) instanceof String str) {
							currencyData.setSell(str);
						} else {
							throw new RuntimeException("Unsupported value type: " + valueList.get(1));
						}

						currencyData.setRatio(1);
						currencyData.setSource(Sources.ALTCOINS.getID());
						currencyData.setDate(updateDate);
						result.add(currencyData);
					} catch (JsonParseException | IndexOutOfBoundsException e) {
						log.warn("Failed on {}, Exception={}", next.getKey(), e.getMessage());
						getReporter().write(TAG_NAME, "Failed with '{}' while parsing '{}' value: {}", e.getMessage(),
								next.getKey(), (String) next.getValue());
					}
				} else if (log.isInfoEnabled()) {
					log.info("Skipped unsupported crypto: {}", next.getKey());
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

				AltcoinsSource.this.close();
				callback.onFailed(t);
			}

			@Override
			public void onRequestCompleted(HttpResponseWrapper response, boolean isCanceled) {
				var result = new ArrayList<CurrencyData>();

				if (!isCanceled) {
					try (var input = new ByteArrayInputStream(response.content())) {
						result.addAll(getAltcoinsRates(input));
					} catch (IOException | ParseException e) {
						log.error("Could not parse source data!", e);
						getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
					}
				} else {
					log.warn("Request was canceled! No currencies were downloaded.");
					getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
				}

				AltcoinsSource.this.close();
				callback.onCompleted(result);
			}
		});
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}
}
