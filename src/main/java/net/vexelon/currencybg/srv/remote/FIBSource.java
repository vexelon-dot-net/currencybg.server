package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
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

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class FIBSource extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(FIBSource.class);
    private static final String TAG_NAME = FIBSource.class.getSimpleName();

    private static final String URL_SOURCE = "https://www.fibank.bg/bg/valutni-kursove";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    public FIBSource(Reporter reporter) {
        super(reporter);
    }

    public List<CurrencyData> getFIBRates(InputStream input) throws IOException, ParseException {
        List<CurrencyData> result = Lists.newArrayList();

        Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

        try {
            Element contentBox = doc.select(
                    "body > div.site > main > section:nth-child(3) > div > div.text > p > small").first();

            // parse update date and time
            String rawDate = contentBox.textNodes().get(0).text().substring(3).trim();
            Date updateDate = DateTimeUtils.parseDate(rawDate, DATE_FORMAT);

            // parse table with currencies
            Element tbody = doc.select("table > tbody").first();
            Elements children = tbody.children();

            int row = 1;

            for (Element child : children) {
                try {
                    CurrencyData currencyData = new CurrencyData();
                    currencyData.setDate(updateDate);
                    currencyData.setCode(child.child(1).text());
                    currencyData.setBuy(child.child(4).text());
                    currencyData.setSell(child.child(5).text());
                    currencyData.setRatio(Integer.parseInt(child.child(2).text()));
                    currencyData.setSource(Sources.FIB.getID());
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

                    FIBSource.this.close();
                    callback.onFailed(e);
                }

                @Override
                public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                    List<CurrencyData> result = Lists.newArrayList();

                    if (!isCanceled) {
                        try {
                            result = getFIBRates(response.getEntity().getContent());
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

        } catch (URISyntaxException e) {
            throw new SourceException("Invalid source url - " + URL_SOURCE, e);
        }
    }

    @Override
    public String getName() {
        return TAG_NAME;
    }

}
