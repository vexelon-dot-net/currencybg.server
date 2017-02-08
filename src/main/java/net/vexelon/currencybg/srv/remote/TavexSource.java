package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class TavexSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(TavexSource.class);
	private static final String TAG_NAME = TavexSource.class.getSimpleName();

	private static final String URL_SOURCE = "https://tavex.bg/obmen-na-valuta";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public TavexSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms Tavex HTML data into {@link CurrencyData} models.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<CurrencyData> getTavexRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		// Parse update date
		String[] components = doc.select("div.timer.timer--flexible.calculator__timer").get(0).text().substring(31)
		        .replaceAll("\\s+", "").split(",");
		Date updateDate = DateTimeUtils.parseDate(components[0] + " " + components[1], DATE_FORMAT);

		// Parse table with currencies
		Elements span = doc.select("div.table-flex__body").get(0).children();

		int row = 0;
		for (Element spanChild : span) {
			CurrencyData currencyData = new CurrencyData();
			try {
				currencyData.setCode(spanChild.child(0).child(1).text());
				currencyData.setBuy(spanChild.child(1).text());
				if ("-".equals(currencyData.getBuy())) {
					currencyData.setBuy("");
				}
				currencyData.setSell(spanChild.child(2).text());
				if ("-".equals(currencyData.getSell())) {
					currencyData.setSell("");
				}
				currencyData.setRatio(1);
				currencyData.setSource(Sources.TAVEX.getID());
				currencyData.setDate(updateDate);
				result.add(currencyData);
			} catch (IndexOutOfBoundsException e) {
				log.warn("Failed on row='{}', Exception={}", row, e.getMessage());
				getReporter().write(TAG_NAME, "Could not process currency on row='{}'!", row + "");
			}

			row++;
		}

		return normalizeCurrencyData(result);
	}

	@Override
	public void getRates(final Callback callback) throws SourceException {
		try {
			final AbstractSource source = this;

			doGet(URL_SOURCE, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

					source.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							result = getTavexRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					source.close();
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
