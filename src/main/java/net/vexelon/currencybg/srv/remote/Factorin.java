package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
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

public class Factorin extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(Factorin.class);
    private static final String TAG_NAME = Factorin.class.getSimpleName();

    private static final String URL_SOURCE = "http://www.factorin.bg/bg/clients/currency/";

    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    private static final Set<String> CURRENCY_CODES = Sets.newHashSet("AED", "ALL", "AUD", "BAM", "BRL", "CAD", "CHF",
            "CNY", "CZK", "DKK", "DOP", "EEK", "EGP", "EUR", "GBP", "HKD", "HRK", "HUF", "IDR", "ILS", "JPY", "KES",
            "KRW", "LTL", "LVL", "MKD", "MUR", "MXN", "MYR", "NOK", "NZD", "PLN", "RON", "RSD", "RUB", "SBP", "SEK",
            "SGD", "THB", "TRY", "UAH", "USD", "ZAR");

    public Factorin(Reporter reporter) {
        super(reporter);
    }

    public List<CurrencyData> getFactorinRates(InputStream input) throws IOException, ParseException {
        List<CurrencyData> result = Lists.newArrayList();

        Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

        try {
            Elements contentBox = doc.select("div.public_list");

            // parse update date and time
            String getDate = contentBox.select("div.public_list > table > tbody > tr > td").text().substring(23);
            Date updateDate = DateTimeUtils.parseDate(getDate, DATE_FORMAT);

            // Parse table with currencies
            Elements contentBoxChildren = contentBox.select("div.currency_list.search").get(0).children();

            int row = 0;

            for (Element child : contentBoxChildren) {
                try {
                    if (CURRENCY_CODES.contains(child.child(1).text())) {
                        CurrencyData currencyData = new CurrencyData();
                        currencyData.setDate(updateDate);
                        currencyData.setCode(child.child(1).text());
                        currencyData.setBuy(child.child(3).text());
                        currencyData.setSell(child.child(4).text());
                        currencyData.setRatio(1);
                        currencyData.setSource(Sources.FACTORIN.getID());
                        result.add(currencyData);
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
