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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @deprecated No longer operational. Absorbed by dskbank.bg.
 */
public class SocieteGenerale extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(SocieteGenerale.class);
	private static final String TAG_NAME = SocieteGenerale.class.getSimpleName();

	private static final String URL_SOURCE  = "https://www.expressbank.bg/bg/byrzi-vryzki/valutni-kursove.html";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public SocieteGenerale(Reporter reporter) {
		super(reporter);
	}

	public List<CurrencyData> getSocieteGeneraleRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			// Parse date
			String currentTimeSofia = LocalTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern("HH:mm")).toString();

			Element dateAttribute = doc.select(
					"div.layout-3-4.text-resize > div.layout-2-4.last.text-resize > h2.heading-normal").first();
			String currentDateTime =
					dateAttribute.text().substring(dateAttribute.text().length() - 10).replace("-", ".") + " "
							+ currentTimeSofia;
			Date updateDate = DateTimeUtils.parseDate(currentDateTime, DATE_FORMAT);

			// Parse data content
			Element content = doc.select("div.layout-2-4.last.text-resize > div.text.text-resize > table > tbody")
					.first();
			Elements children = content.children();

			int row = 1;

			for (Element child : children) {
				CurrencyData currencyData = new CurrencyData();
				try {
					if (row > 0) {
						currencyData.setDate(updateDate);
						currencyData.setCode(child.child(1).text());
						currencyData.setBuy(child.child(3).text());
						currencyData.setSell(child.child(4).text());
						currencyData.setRatio(1);
						currencyData.setSource(Sources.SGEB.getID());
						result.add(currencyData);
					}
				} catch (IndexOutOfBoundsException e) {
					log.warn("Failed on row={}, Exception={}", row, e.getMessage());
					getReporter().write(TAG_NAME, "Could not process currency on row={}!", row + "");
				}

				row++;
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

					SocieteGenerale.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							result = getSocieteGeneraleRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}
					SocieteGenerale.this.close();
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
