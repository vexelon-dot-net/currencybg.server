package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

	public CryptoBGSource(Vertx vertx, Reporter reporter) {
		super(vertx, reporter);
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
					var code = StringUtils.substringBefore(span.child(0).text(), "/").strip();
					// CryptoBG has the same rates for Tether and USD coin
					code = switch (code) {
						case "USDC(T)" -> Defs.CURRENCY_USD_COIN;
						default -> code;
					};

					if (Defs.CURRENCY_CODES_CRYPTO.contains(code)) {
						try {
							var currencyData = new CurrencyData();
							currencyData.setCode(code);
							currencyData.setBuy(StringUtils.remove(span.child(1).text(), ","));
							currencyData.setSell(StringUtils.remove(span.child(2).text(), ","));
							currencyData.setRatio(1);
							currencyData.setSource(Sources.CRYPTO.getID());
							currencyData.setDate(updateDate);
							result.add(currencyData);

							if (Defs.CURRENCY_USD_COIN.equals(currencyData.getCode())) {
								// CryptoBG has the same rates for Tether and USD coin
								result.add(new CurrencyData(Defs.CURRENCY_TETHER, currencyData.getRatio(),
										currencyData.getBuy(), currencyData.getSell(), currencyData.getDate(),
										currencyData.getSource()));
							}
						} catch (Exception e) {
							log.warn("Failed on {}, Exception={}", code, e.getMessage());
							getReporter().write(TAG_NAME, "Could not process crypto {}", code);
						}
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
		doGet(URL_SOURCE, new HTTPCallback() {

			@Override
			public void onRequestFailed(Throwable t) {
				getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(t));

				CryptoBGSource.this.close();
				callback.onFailed(t);
			}

			@Override
			public void onRequestCompleted(HttpResponseWrapper response, boolean isCanceled) {
				var result = new ArrayList<CurrencyData>();

				if (!isCanceled) {
					try (var input = new ByteArrayInputStream(response.content())) {
						result.addAll(getCryptoRates(input));
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
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}
}
