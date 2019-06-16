package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class Factorin extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(Factorin.class);
    private static final String TAG_NAME = Factorin.class.getSimpleName();

    private static final String URL_SOURCE = "https://www.factorin.bg/bg/clients/currency/";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    public Factorin(Reporter reporter) {
        super(reporter);
    }

    public List<CurrencyData> getFactorinRates(InputStream input) throws IOException, ParseException {
        List<CurrencyData> result = Lists.newArrayList();

        Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

        try {
            // parse update date and time
            // e.g. Последно обновяване на цените: 15.06.2019 13:09
            Elements dateContent = doc.select("#currency-calc > div > div.info > p");
            String getDate = StringUtils.substringAfter(dateContent.text(), ":").trim();
            Date updateDate = DateTimeUtils.parseDate(getDate, DATE_FORMAT);

            // Parse table with currencies
            Elements contentBoxChildren = doc.select(
                    "div.currencies-table.currencies > table:nth-child(2) > tbody").get(0).children();

            int row = 0;

            for (Element child : contentBoxChildren) {
                try {
                    final Elements td = child.getElementsByClass("name");
                    if (!td.isEmpty()) {
                        final Element element = td.get(0);
                        if (Defs.CURRENCY_CODES_APP.contains(element.attr("data-code"))) {
                            CurrencyData currencyData = new CurrencyData();
                            currencyData.setSource(Sources.FACTORIN.getID());
                            currencyData.setDate(updateDate);
                            currencyData.setRatio(1);
                            currencyData.setCode(element.attr("data-code"));
                            currencyData.setBuy(element.attr("data-price-buy"));
                            currencyData.setSell(element.attr("data-price-sell"));
                            result.add(currencyData);
                        }
                    }
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

                    Factorin.this.close();
                    callback.onFailed(e);
                }

                @Override
                public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                    List<CurrencyData> result = Lists.newArrayList();

                    if (!isCanceled) {
                        try {
                            result = getFactorinRates(response.getEntity().getContent());
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
