package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import java.util.Date;
import java.util.List;

/**
 * @deprecated No longer operational. Cryptobank.bg is now owned by Altcoins.bg.
 */
public class CryptoBankSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(CryptoBankSource.class);
	private static final String TAG_NAME = CryptoBankSource.class.getSimpleName();

	private static final String URL_SOURCE  = "https://cryptobank.bg";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public CryptoBankSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms Crypto Bank HTML data into {@link CurrencyData} models.
	 *
	 * @return Parsed currencies.
	 */
	public List<CurrencyData> getCryptoBankRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

			Element span = doc.select("div.jsn-modulecontent > table > tbody").get(0);
			String sanitized = sanitize(span.text());

			// Bitcoin (BTC/BGN)  23,720.07 .  24,688.23 . Ether (ETH/BGN)  751.61 .  786.96 . Litecoin (LTC/BGN)  242.03 .  254.44 . Dash (DASH/BGN)  1,151.16 .  1,210.20 . Bitcoin cash(BCH/BGN)  2,210.52 .  2,323.88 . Dogecoin (DOGE/BGN)  0.004213 .  0.004501

			final String[] splitters = new String[] { "Dogecoin", "Bitcoin cash", "Dash", "Litecoin", "Ether",
					"Bitcoin" };

			for (String delimiter : splitters) {
				String[] split = StringUtils.splitByWholeSeparator(sanitized, delimiter);

				if (!ArrayUtils.isEmpty(split)) {
					String payload;

					if (split.length > 1) {
						payload = split[1];
						sanitized = split[0];
					} else {
						payload = split[0];
						sanitized = "";
					}

					CurrencyData currencyData = toCurrencyData(payload);
					if (currencyData != null) {
						currencyData.setSource(Sources.CRYPTOBANK.getID());
						currencyData.setDate(updateDate);
						result.add(currencyData);
					}
				}
			}

			return normalizeCurrencyData(result);
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
	}

	private CurrencyData toCurrencyData(String payload) {
		// e.g., payload="Dogecoin (DOGE/BGN)  0.004213 .  0.004501"

		CurrencyData result = new CurrencyData();

		if (payload.indexOf("DOGE") > 0) {
			result.setCode(Defs.CURRENCY_DOGECOIN);
		} else if (payload.indexOf("BCH") > 0) {
			result.setCode(Defs.CURRENCY_BITCOIN_CASH);
		} else if (payload.indexOf("DASH") > 0) {
			result.setCode(Defs.CURRENCY_DASH);
		} else if (payload.indexOf("LTC") > 0) {
			result.setCode(Defs.CURRENCY_LITECOIN);
		} else if (payload.indexOf("ETH") > 0) {
			result.setCode(Defs.CURRENCY_ETHERIUM);
		} else if (payload.indexOf("BTC") > 0) {
			result.setCode(Defs.CURRENCY_BITCOIN);
		} else {
			getReporter().write(TAG_NAME, "Cannot parse payload='{}'", payload);
			return null;
		}

		String[] split = payload.split(" ");
		boolean isSell = false;

		for (String next : split) {
			if (!isSell && ".".equals(next)) {
				isSell = true;
			} else if (!next.isEmpty() && !".".equals(next)) {
				next = StringUtils.remove(next, ',');

				if (isNumeric(next)) {
					if (isSell) {
						result.setSell(next);
					} else {
						result.setBuy(next);
					}
				}
			}
		}

		result.setRatio(1);

		return result;
	}

	private static String sanitize(String value) {
		String result = StringUtils.trimToEmpty(value.replaceAll("\\P{Print}", ""));
		result = StringUtils.removeStart(result, ":");
		result = StringUtils.removeEnd(result, ".");
		return StringUtils.trimToEmpty(result);
	}

	private static boolean isNumeric(final CharSequence cs) {
		if (StringUtils.isEmpty(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false && cs.charAt(i) != '.') {
				return false;
			}
		}
		return true;
	}

	@Override
	public void getRates(Callback callback) throws SourceException {
		try {
			doGet(URL_SOURCE, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

					CryptoBankSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = new ArrayList<>();

					if (!isCanceled) {
						try {
							result = getCryptoBankRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {} ", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					CryptoBankSource.this.close();
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
