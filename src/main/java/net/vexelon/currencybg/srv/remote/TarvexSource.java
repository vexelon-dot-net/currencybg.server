package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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
import net.vexelon.currencybg.srv.db.models.CurrencyLocales;

public class TarvexSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(TarvexSource.class);

	public final static String URL_SOURCE = "http://www.tavex.bg/?main=24";

	public TarvexSource(Callback callback) {
		super(callback);
	}

	private void getRates(final String sourceUrl) throws SourceException {
		try {
			doGet(sourceUrl, new ResponseCallback() {

				@Override
				public void onReqFailed(Exception e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReqCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();
					if (!isCanceled) {
						try {
							Document doc = Jsoup.parse(response.getEntity().getContent(), Charsets.UTF_8.name(),
									sourceUrl);
							Element div = doc
									.select("#page-sub-content > tbody > tr > td.right > table:nth-child(5) > tbody")
									.first();
							Elements divChildren = div.children();
							for (Element el : divChildren) {
								System.out.println(el.text());
							}
							callback.onCompleted(result); // TODO
						} catch (IOException e) {

						}
					} else {
						// TODO
					}
				}
			});
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map<CurrencyLocales, List<CurrencyData>> downloadRates() throws SourceException {
		getRates(URL_SOURCE);
		return null;
	}

	public static void main(String[] args) {
		try {
			new TarvexSource(new Callback() {

				@Override
				public void onCompleted(List<CurrencyData> currencyDataList) {
					// TODO Auto-generated method stub

				}
			}).downloadRates();
		} catch (SourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
