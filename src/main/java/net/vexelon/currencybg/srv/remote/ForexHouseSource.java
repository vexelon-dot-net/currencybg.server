package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @deprecated No longer operational.
 */
public class ForexHouseSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(ForexHouseSource.class);
	private static final String TAG_NAME = ForexHouseSource.class.getSimpleName();

	private static final String URL_SOURCE  = "http://forexhouse.eu";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public ForexHouseSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms ForexHouse HTML data into {@link CurrencyData} models.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<CurrencyData> getForexHouseRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {

			String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

			// Parse table with currencies
			Elements mainCurrency = doc.select("#tablepress-3 > tbody").first().children();
			int row = 0;// The first row is for BTC which there is no sell and
			// price
			String[] firstColumn;// It contains code + ratio
			for (Element child : mainCurrency) {
				CurrencyData currencyData = new CurrencyData();
				firstColumn = child.child(2).text().split(" ");
				try {
					if (row > 0) {
						currencyData.setDate(updateDate);
						currencyData.setCode(firstColumn[1]);
						currencyData.setBuy(child.child(3).text());
						currencyData.setSell(child.child(4).text());
						currencyData.setRatio(Integer.parseInt(firstColumn[0]));
						currencyData.setSource(Sources.FOREXHOUSE.getID());
						result.add(currencyData);

					}

				} catch (IndexOutOfBoundsException e) {
					log.warn("Failed on row='{}', Exception={}", row, e.getMessage());
					getReporter().write(TAG_NAME, "Could not process currency on row='{}'!", row + "");
				}

				row++;
			}

			Elements euroCurrency = doc.select("#tablepress-6 > tbody").first().children();
			for (Element child : euroCurrency) {
				CurrencyData currencyData = new CurrencyData();
				firstColumn = child.child(2).text().split(" ");
				try {
					if (child.child(3).text().length() > 0 && child.child(4).text().length() > 0) {
						currencyData.setDate(updateDate);
						currencyData.setCode(firstColumn[1]);
						currencyData.setBuy(child.child(3).text().replaceAll("\\s+", ""));
						currencyData.setSell(child.child(4).text());
						currencyData.setRatio(Integer.parseInt(firstColumn[0]));
						currencyData.setSource(Sources.FOREXHOUSE.getID());
						result.add(currencyData);

					}

				} catch (IndexOutOfBoundsException e) {
					log.warn("Failed on row='{}', Exception={}", row, e.getMessage());
					getReporter().write(TAG_NAME, "Could not process currency on row='{}'!", row + "");
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

					ForexHouseSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							result = getForexHouseRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					ForexHouseSource.this.close();
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
