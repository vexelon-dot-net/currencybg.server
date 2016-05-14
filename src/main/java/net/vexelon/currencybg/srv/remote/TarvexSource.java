package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencyLocales;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class TarvexSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(TarvexSource.class);

	private final static String URL_SOURCE = "http://www.tavex.bg/?main=24";
	private final static String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	public TarvexSource(Callback callback) {
		super(callback);
	}

	private void getRates(final String sourceUrl) throws SourceException {
		try {
			final AbstractSource source = this;
			doGet(sourceUrl, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					// TODO Auto-generated method stub
					callback.onCompleted(new ArrayList<CurrencyData>());

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
									currencyData.setCurrDate(updateDate);
									currencyData.setCode(tr.child(1).text());
									currencyData.setRate(tr.child(3).text());
									currencyData.setReverseRate(tr.child(4).text());
									currencyData.setRatio(1);
								} catch (IndexOutOfBoundsException e) {
									// TODO report
								}
							}
						} catch (IOException | ParseException e) {
							// TODO report
							log.error("Could not parse source data!", e);
						}
					} else {
						// TODO report
						log.warn("Request was canceled! No currencies were downloaded.");
					}

					callback.onCompleted(result); // TODO

					IOUtils.closeQuietly(source);
				}
			});
		} catch (URISyntaxException e) {
			throw new SourceException("Invalid source url - " + sourceUrl, e);
		}
	}

	@Override
	public Map<CurrencyLocales, List<CurrencyData>> downloadRates() throws SourceException {
		getRates(URL_SOURCE);
		return null;
	}

	public static void main(String[] args) {
		try {
			final TarvexSource tarvexSource = new TarvexSource(new Callback() {

				@Override
				public void onCompleted(List<CurrencyData> currencyDataList) {
					// TODO Auto-generated method stub
					// IOUtils.closeQuietly(tarvexSource);
				}
			});
			tarvexSource.downloadRates();
		} catch (SourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
