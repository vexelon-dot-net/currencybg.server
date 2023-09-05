package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CryptoBGSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(CryptoBGSource.class);
	private static final String TAG_NAME = CryptoBGSource.class.getSimpleName();

	private static final String URL_SOURCE  = "https://crypto.bg/tickers_header";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	private String htmlData;

	public CryptoBGSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms Crypto BG HTML data into {@link CurrencyData} models
	 */
	public List<CurrencyData> getCryptoRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		var doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);
		htmlData = doc.toString(); // debugging

		try {
			var updateDate = DateTimeUtils.parseDate(LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), DATE_FORMAT);

			// Parse table with currencies
			for (var span : doc.select("table > tbody > tr")) {
				if (!span.children().isEmpty()) {
					var code = StringUtils.substringBefore(span.child(0).text(), " ");
					if (Defs.CURRENCY_CODES_CRYPTO.contains(code)) {
						var currencyData = new CurrencyData();
						currencyData.setCode(code);
						currencyData.setBuy(StringUtils.remove(span.child(1).text(), ","));
						currencyData.setSell(StringUtils.remove(span.child(2).text(), ","));
						currencyData.setRatio(1);
						currencyData.setSource(Sources.CRYPTO.getID());
						currencyData.setDate(updateDate);
						result.add(currencyData);
					} else {
						log.info("Skipped unsupported crypto: {}", code);
					}
				}
			}

			return normalizeCurrencyData(result);
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void getRates(Callback callback) throws SourceException {
		try {
			doGet(URL_SOURCE, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

					CryptoBGSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					var result = new ArrayList<CurrencyData>();

					if (!isCanceled) {
						try {
							result.addAll(getCryptoRates(response.getEntity().getContent()));
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}  HTML= {}", ExceptionUtils.getStackTrace(e),
									htmlData);
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					CryptoBGSource.this.close();
					callback.onCompleted(result);
				}
			});
		} catch (URISyntaxException e) {
			throw new SourceException("Invalid source url - " + URL_SOURCE, e);
		}
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}
}
