package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Polana1 extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(Polana1.class);
	private static final String TAG_NAME = Polana1.class.getSimpleName();

	private static final String URL_SOURCE  = "https://polana1.com/bg/%D0%BE%D0%B1%D0%BC%D1%8F%D0%BD%D0%B0-%D0%BD%D0%B0-%D0%B2%D0%B0%D0%BB%D1%83%D1%82%D0%B0";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public Polana1(Reporter reporter) {
		super(reporter);
	}

	public List<CurrencyData> getPolana1(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		var doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			var contentBox = doc.select("div.content-center ").first();
			var contentChildren = contentBox.children();

			String currentTimeSofia = LocalTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern("HH:mm"));

			String currentDateTime =
					contentChildren.select("h1").first().text().substring(26, 37).trim() + " " + currentTimeSofia;
			var updateDate = DateTimeUtils.parseDate(currentDateTime, DATE_FORMAT);

			int row = 0;

			for (var child : Optional.ofNullable(contentChildren.select("table > tbody").first())
					.orElseThrow(() -> new RuntimeException("table > tbody was not found!")).children()) {
				row++;

				try {
					var currencyData = new CurrencyData();
					currencyData.setDate(updateDate);
					currencyData.setCode(child.child(1).text().replace("\u00a0", ""));
					currencyData.setBuy(child.child(3).text().replace("\u00a0", ""));
					currencyData.setSell(child.child(4).text().replace("\u00a0", ""));
					currencyData.setRatio(Integer.parseInt(child.child(2).text().replace("\u00a0", "")));
					currencyData.setSource(Sources.POLANA1.getID());

					currencyData.setBuy(currencyData.getBuy().replace("-", ""));
					currencyData.setSell(currencyData.getSell().replace("-", ""));
					if ("0.".equals(currencyData.getBuy())) {
						currencyData.setBuy("");
					}
					if ("0.".equals(currencyData.getSell())) {
						currencyData.setSell("");
					}

					result.add(currencyData);
				} catch (IndexOutOfBoundsException e) {
					log.warn("Failed on row={}, Exception={}", row, e.getMessage());
					getReporter().write(TAG_NAME, "Could not process currency on row={}!", row + "");
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

					Polana1.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					var result = new ArrayList<CurrencyData>();

					if (!isCanceled) {
						try {
							result.addAll(getPolana1(response.getEntity().getContent()));
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					Polana1.this.close();
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
