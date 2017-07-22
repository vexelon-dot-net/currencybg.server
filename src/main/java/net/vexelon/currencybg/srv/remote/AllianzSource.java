package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class AllianzSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(AllianzSource.class);
	private static final String TAG_NAME = AllianzSource.class.getSimpleName();

	private static final String URL_SOURCE = "https://www.allianz.bg/currency_rates/";

	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public AllianzSource(Reporter reporter) {
		super(reporter);
	}

	public List<CurrencyData> getAllianzRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			Elements contentBox = doc.select("div.inner > table > tbody").get(0).children();

			String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
			        .format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

			int row = 0;

			for (Element child : contentBox) {
				try {
					CurrencyData currencyData = new CurrencyData();
					currencyData.setDate(updateDate);
					currencyData.setCode(child.child(0).text());
					currencyData.setBuy(child.child(1).text());
					currencyData.setSell(child.child(2).text());
					currencyData.setRatio(1);
					currencyData.setSource(Sources.ALLIANZ.getID());
					result.add(currencyData);

				} catch (IndexOutOfBoundsException e) {
					log.warn("Failed on row='{}', Exception={}", row, e.getMessage());
					getReporter().write(TAG_NAME, "Could not process currency on row='{}'!", row + "");
				}

				row++;
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

					AllianzSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							result = getAllianzRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					AllianzSource.this.close();
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
