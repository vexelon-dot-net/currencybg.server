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

public class CryptoBankSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(CryptoBankSource.class);
	private static final String TAG_NAME = CryptoBankSource.class.getSimpleName();

	private static final String URL_SOURCE = "https://cryptobank.bg/index.php";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	private String htmlData;

	public CryptoBankSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms Crypto Bank HTML data into {@link CurrencyData} models.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<CurrencyData> getCryptoBankRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);
		htmlData = doc.toString(); // debugging

		try {
			String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
			        .format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

			// Parse table with currencies
			Element span = doc.select("div.jsn-modulecontent > table > tbody").get(1);
			Elements spanContent = span.children();
			int counter = 0;
			CurrencyData currencyData = new CurrencyData();
			String currency;
			for (Element element : spanContent) {
				if (counter > 0) {
					currency = element.child(0).select("td > a > img").attr("alt").split(" ")[0];
					switch (currency) {
					case "bitcoin":
						currencyData.setCode(Defs.CURRENCY_BITCOIN);
						break;
					case "ether":
						currencyData.setCode(Defs.CURRENCY_ETHERIUM);
						break;
					case "litecoin":
						currencyData.setCode(Defs.CURRENCY_LITECOIN);
						break;
					case "bitcoin cash":
						currencyData.setCode(Defs.CURRENCY_BITCOIN_CASH);
						break;
					case "dash":
						currencyData.setCode(Defs.CURRENCY_DASH);
						break;
					case "dogecoin":
						currencyData.setCode(Defs.CURRENCY_DOGECOIN);
						break;
					}
					// The Replace statement(.replace(",", "")) have been made
					// for BTC to change the format from 6,959.60 to 6959.60
					currencyData.setBuy(element.child(1).text().substring(0, element.child(1).text().indexOf(" "))
					        .replace(",", ""));
					currencyData.setSell(element.child(2).text().substring(0, element.child(2).text().indexOf(" "))
					        .replace(",", ""));
					currencyData.setRatio(1);
					currencyData.setSource(Sources.CRYPTOBANK.getID());
					currencyData.setDate(updateDate);
					result.add(currencyData);

					currencyData = new CurrencyData();
				}
				counter++;
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

					CryptoBankSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {

							result = getCryptoBankRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}  HTML= {}", ExceptionUtils.getStackTrace(e),
			                        htmlData);
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					CryptoBankSource.this.close();
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
