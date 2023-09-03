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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	 */
	public List<CurrencyData> getTavexRates(InputStream input) throws IOException, ParseException {
		var result = new ArrayList<CurrencyData>();

		var doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

		try {
			var updateDate = DateTimeUtils.parseDate(LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT)), DATE_FORMAT);

			// Parse currencies
			int row = 0;

			for (var spanChild : Objects.requireNonNull(
					doc.select("div.currency-hero__rates table.list-table.list-table--full > tbody").first(),
					"div.currency-hero__rates table.list-table.list-table--full was not found!").children()) {

				var currencyData = new CurrencyData();
				try {
					var childs = spanChild.children();

					var code = childs.get(0).child(0).text().strip();
					var name = childs.get(0).child(1).text().strip();

					currencyData.setCode(code);
					currencyData.setRatio(name.endsWith(")") ?
							Integer.parseInt(
									StringUtils.substringBeforeLast(StringUtils.substringAfterLast(name, "("), ")")) :
							1);

					if (childs.size() > 1) {
						currencyData.setBuy(childs.get(1).text().strip());
						if ("-".equals(currencyData.getBuy())) {
							currencyData.setBuy("");
						}
					}

					if (childs.size() > 2) {
						currencyData.setSell(childs.get(2).text().strip());
						if ("-".equals(currencyData.getSell())) {
							currencyData.setSell("");
						}
					}

					currencyData.setSource(Sources.TAVEX.getID());
					currencyData.setDate(updateDate);
					result.add(currencyData);
				} catch (IndexOutOfBoundsException e) {
					log.warn("Failed on row={}, Exception={}", row, e.getMessage());
					getReporter().write(TAG_NAME, "Could not process currency on row={}, code={}", row + "",
							currencyData.getCode());
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

					TavexSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					var result = new ArrayList<CurrencyData>();

					if (!isCanceled) {
						try {
							result.addAll(getTavexRates(response.getEntity().getContent()));
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
