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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Factorin extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(Factorin.class);
	private static final String TAG_NAME = Factorin.class.getSimpleName();

	private static final String URL_SOURCE  = "https://www.factorin.bg/bg/clients/currency/";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public Factorin(Reporter reporter) {
		super(reporter);
	}

	public List<CurrencyData> getFactorinRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		var doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			// parse date-time
			var dateHtml = doc.select("#currency-calc > div > div.info > p");
			String dateAsText = StringUtils.substringAfter(dateHtml.text(), ":").strip();
			if (dateAsText.isEmpty()) {
				log.warn("Parsing skipped! Could not find date in html contents: {}", dateHtml.text());
				return result;
			}
			var updateDate = DateTimeUtils.parseDate(dateAsText, DATE_FORMAT);

			// parse currencies
			var contentBoxChildren = Stream.concat(
					// 1st table
					Objects.requireNonNull(
							doc.select("div.currencies-table.currencies > table:nth-child(2) > tbody").first(),
							"table:nth-child(2) was not found!").children().stream(),
					// 2nd table
					Objects.requireNonNull(
							doc.select("div.currencies-table.currencies > table:nth-child(3) > tbody").first(),
							"table:nth-child(3) was not found!").children().stream());

			int row = 1;

			for (var it = contentBoxChildren.iterator(); it.hasNext(); ) {
				var child = it.next();
				var nameTds = child.getElementsByClass("name");

				if (!nameTds.isEmpty()) {
					var td = nameTds.get(0);
					var code = td.attr("data-code");

					if (Defs.CURRENCY_CODES_APP.contains(code)) {
						try {
							var currencyData = new CurrencyData();
							currencyData.setSource(Sources.FACTORIN.getID());
							currencyData.setDate(updateDate);
							currencyData.setRatio(1);
							currencyData.setCode(code);
							currencyData.setBuy(td.attr("data-price-buy"));
							currencyData.setSell(td.attr("data-price-sell"));
							result.add(currencyData);
						} catch (Exception e) {
							log.warn("Failed on row={} ({}), Exception={}", row, code, e.getMessage());
							getReporter().write(TAG_NAME, "Could not process currency on row={} with code={}!",
									row + "", code);
						}
					}
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

					Factorin.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					var result = new ArrayList<CurrencyData>();

					if (!isCanceled) {
						try {
							result.addAll(getFactorinRates(response.getEntity().getContent()));
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					Factorin.this.close();
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
