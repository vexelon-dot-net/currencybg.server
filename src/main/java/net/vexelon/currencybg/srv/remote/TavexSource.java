package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class TavexSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(TavexSource.class);
	private static final String TAG_NAME = TavexSource.class.getSimpleName();

	private static final String URL_SOURCE = "http://www.tavex.sbg/?main=24";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public TavexSource(Callback callback, Reporter reporter) {
		super(callback, reporter);
	}

	private void getRates(final String sourceUrl) throws SourceException {
		try {
			final AbstractSource source = this;
			doGet(sourceUrl, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					// TODO Auto-generated method stub
					callback.onCompleted(new ArrayList<CurrencyData>());

					try {
						getReporter().write(TAG_NAME, "Connection failed= {}", ExceptionUtils.getStackTrace(e));
						getReporter().send();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					IOUtils.closeQuietly(source);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();
					if (!isCanceled) {
						try {
							Document doc = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(),
									sourceUrl);

							// parse update date
							Date updateDate;
							Element span = doc.select("#page-sub-content > tbody > tr > td.right > span").first();
							String[] components = StringUtils.split(span.text(), " ", 2);
							if (components.length > 0) {
								updateDate = DateTimeUtils.parseStringToDate(components[1], DATE_FORMAT);
							} else {
								throw new ParseException("Could not parse date - " + span.text(), 0);
							}

							// parse list of currencies
							Element tbody = doc
									.select("#page-sub-content > tbody > tr > td.right > table:nth-child(5) > tbody")
									.first();
							for (Element tr : tbody.children()) {
								CurrencyData currencyData = new CurrencyData();
								try {
									currencyData.setDate(updateDate);
									currencyData.setCode(tr.child(1).text());
									currencyData.setBuy(tr.child(3).text());
									currencyData.setSell(tr.child(4).text());
									currencyData.setRatio(1);
								} catch (IndexOutOfBoundsException e) {
									log.warn("Failed on row='{}', Exception={}", tr.text(), e.getMessage());
									getReporter().write(TAG_NAME, "Could not process currency on row='{}'!", tr.text());
								}
							}
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					callback.onCompleted(result); // TODO
					try {
						getReporter().send();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					IOUtils.closeQuietly(source);
				}
			});
		} catch (URISyntaxException e) {
			throw new SourceException("Invalid source url - " + sourceUrl, e);
		}
	}

	@Override
	public List<CurrencyData> downloadRates() throws SourceException {
		getRates(URL_SOURCE);
		return null;
	}

	public static void main(String[] args) {
		try {
			final TavexSource tavexSource = new TavexSource(new Callback() {

				@Override
				public void onCompleted(List<CurrencyData> currencyDataList) {
					// TODO Auto-generated method stub
					// IOUtils.closeQuietly(tarvexSource);

				}
			}, new ConsoleReporter());

			tavexSource.downloadRates();

		} catch (SourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
