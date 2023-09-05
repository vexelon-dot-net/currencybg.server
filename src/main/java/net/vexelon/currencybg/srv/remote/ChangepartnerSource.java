package net.vexelon.currencybg.srv.remote;

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

public class ChangepartnerSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(ChangepartnerSource.class);
	private static final String TAG_NAME = ChangepartnerSource.class.getSimpleName();

	private static final String URL_SOURCE  = "http://www.changepartner.net/valuta.aspx";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public ChangepartnerSource(Reporter reporter) {
		super(reporter);
	}

	public List<CurrencyData> getChangepartnerRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		var doc = Jsoup.parse(input, "windows-1251", URL_SOURCE);

		try {
			var updateDate = DateTimeUtils.parseDate(LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), DATE_FORMAT);

			// Parse table with currencies
			for (var spanChild : doc.select("table#MyDataGrid > tbody").get(0).children()) {
				try {
					var currencyData = new CurrencyData();
					// Currency format -
					// <td width="280">������� ����(JPY 100)</td>
					String currency = spanChild.select("tr > td > table.border > tbody > tr > td").get(1).text();
					String currencyActualValue = StringUtils.strip(StringUtils.substringAfterLast(currency, " "), "()");
					if (StringUtils.isAllUpperCase(currencyActualValue)) {
						String currencyCode = currencyActualValue.replaceAll("[^A-Za-z]+", "");

						currencyData.setCode(currencyCode);
						currencyData.setBuy(spanChild.select("tr > td > table.border > tbody > tr > td").get(2).text());
						currencyData.setSell(
								spanChild.select("tr > td > table.border > tbody > tr > td").get(3).text());
						currencyData.setBuy(currencyData.getBuy().strip().replace("-", ""));
						currencyData.setSell(currencyData.getSell().strip().replace("-", ""));

						//					String currencyRatio = currencyActualValue.replaceAll("[^\\d.]", "");
						//					if (!currencyRatio.isEmpty()) {
						//						currencyData.setRatio(Integer.parseInt(currencyRatio));
						//					} else {
						currencyData.setRatio(1);
						//					}

						currencyData.setSource(Sources.CHANGEPARTNER.getID());
						currencyData.setDate(updateDate);

						result.add(currencyData);
					} else {
						log.info("Buy/sell not available or unexpected info present on row: {}", spanChild.text());
					}
				} catch (IndexOutOfBoundsException e) {
					break; // The last row in the array is fake
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

					ChangepartnerSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					var result = new ArrayList<CurrencyData>();

					if (!isCanceled) {
						try {
							result.addAll(getChangepartnerRates(response.getEntity().getContent()));
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					ChangepartnerSource.this.close();
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
