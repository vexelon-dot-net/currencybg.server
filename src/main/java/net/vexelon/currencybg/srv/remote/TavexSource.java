package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

public class TavexSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(TavexSource.class);
	private static final String TAG_NAME = TavexSource.class.getSimpleName();

	private static final String URL_SOURCE  = "https://tavex.bg/obmen-na-valuta";
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

		try {

			String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

			// Parse table with currencies
			Elements wrappers = doc.select("div.table-flex--currency div.table-flex__body");
			for (Element wrapper : wrappers) {
				Elements span = wrapper.children();
				int row = 0;

				for (Element spanChild : span) {
					CurrencyData currencyData = new CurrencyData();
					try {
						currencyData.setCode(spanChild.child(0).child(1).text());
						currencyData.setRatio(NumberUtils.toInt(spanChild.child(1).text(), 1));

						Elements spans = spanChild.child(2).children();
						if (spans.size() > 1) {
							currencyData.setBuy(spans.get(0).text());
							if ("-".equals(currencyData.getBuy())) {
								currencyData.setBuy("");
							}
							currencyData.setSell(spans.get(1).text());
							if ("-".equals(currencyData.getSell())) {
								currencyData.setSell("");
							}
						}

						currencyData.setSource(Sources.TAVEX.getID());
						currencyData.setDate(updateDate);
						result.add(currencyData);
					} catch (IndexOutOfBoundsException e) {
						log.warn("Failed on row={}, Exception={}", row, e.getMessage());
						getReporter().write(TAG_NAME, "Could not process currency on row={}!", row + "");
					}

					row++;
				}
			}

			return normalizeCurrencyData(result);
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void getRates(final Callback callback) throws SourceException {
		try {
			doGet(URL_SOURCE, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

					TavexSource.this.close();
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

					TavexSource.this.close();
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
