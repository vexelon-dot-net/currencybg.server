package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FIBSource extends AbstractSource {

	private static final Logger log         = LoggerFactory.getLogger(FIBSource.class);
	private static final String TAG_NAME    = FIBSource.class.getSimpleName();
	private static final String URL_SOURCE  = "https://www.fibank.bg/bg/valutni-kursove";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public FIBSource(Vertx vertx, Reporter reporter) {
		super(vertx, reporter);
	}

	public List<CurrencyData> getFIBRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		var doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			var contentBox = doc.select("div.text > p > small").first();
			String rawDate = Optional.ofNullable(contentBox).map(Element::text).orElse("");

			// parse update date and time
			var updateDate = rawDate.isBlank() ?
					new Date() :
					DateTimeUtils.parseDate(rawDate.substring(3).trim(), DATE_FORMAT);

			// parse table with currencies
			int row = 1;

			for (var child : Optional.ofNullable(doc.select(".scrollable-content > table > tbody").first())
					.orElseThrow(() -> new RuntimeException(".scrollable-content > table > tbody was not found!"))
					.children()) {
				try {
					var currencyData = new CurrencyData();
					currencyData.setDate(updateDate);
					currencyData.setCode(child.child(1).text());
					currencyData.setBuy(child.child(4).text());
					currencyData.setSell(child.child(5).text());

					// fix XAU formatting for FIBank
					if ("XAU".equalsIgnoreCase(currencyData.getCode())) {
						currencyData.setBuy(currencyData.getBuy().replace(",", ""));
						currencyData.setSell(currencyData.getSell().replace(",", ""));
					}
					currencyData.setRatio(Integer.parseInt(child.child(2).text()));
					currencyData.setSource(Sources.FIB.getID());

					result.add(currencyData);
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
	public void getRates(final Callback callback) throws SourceException {
		doGet(URL_SOURCE, new HTTPCallback() {

			@Override
			public void onRequestFailed(Throwable t) {
				getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(t));

				FIBSource.this.close();
				callback.onFailed(t);
			}

			@Override
			public void onRequestCompleted(HttpResponseWrapper response, boolean isCanceled) {
				var result = new ArrayList<CurrencyData>();

				if (!isCanceled) {
					try (var input = new ByteArrayInputStream(response.content())) {
						result.addAll(getFIBRates(input));
					} catch (IOException | ParseException e) {
						log.error("Could not parse source data!", e);
						getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
					}
				} else {
					log.warn("Request was canceled! No currencies were downloaded.");
					getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
				}

				FIBSource.this.close();
				callback.onCompleted(result);
			}
		});
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}
}
