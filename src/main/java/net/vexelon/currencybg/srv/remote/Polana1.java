package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import net.vexelon.currencybg.srv.reports.ConsoleReporter;
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

public class Polana1 extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(Polana1.class);
    private static final String TAG_NAME = Polana1.class.getSimpleName();

    private static final String URL_SOURCE = "https://polana1.com/bg/%D0%BE%D0%B1%D0%BC%D1%8F%D0%BD%D0%B0-%D0%BD%D0%B0-%D0%B2%D0%B0%D0%BB%D1%83%D1%82%D0%B0";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    public Polana1(Reporter reporter) {
        super(reporter);
    }

    public List<CurrencyData> getPolana1(InputStream input) throws IOException, ParseException {
        List<CurrencyData> result = Lists.newArrayList();

        Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

        try {
            Element contentBox = doc.select("div.content-center ").first();
            Elements contentChildren = contentBox.children();

            String currentTimeSofia = LocalTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
                    .format(DateTimeFormatter.ofPattern("HH:mm")).toString();

            String currentDateTime = contentChildren.select("h1").first().text().substring(26).trim() + " "
                    + currentTimeSofia;
            Date updateDate = DateTimeUtils.parseDate(currentDateTime, DATE_FORMAT);

            Element contentBoxChildren = contentChildren.select("table > tbody").first();
            Elements children = contentBoxChildren.children();

            int row = 0;

            for (Element child : children) {
                row++;

                CurrencyData currencyData = new CurrencyData();
                try {
                    currencyData.setDate(updateDate);
                    currencyData.setCode(child.child(1).text().replace("\u00a0", ""));
                    currencyData.setBuy(child.child(3).text().replace("\u00a0", ""));
                    currencyData.setSell(child.child(4).text().replace("\u00a0", ""));
                    currencyData.setRatio(Integer.parseInt(child.child(2).text().replace("\u00a0", "")));
                    currencyData.setSource(Sources.POLANA1.getID());

                    result.add(currencyData);
                } catch (IndexOutOfBoundsException e) {
                    log.warn("Failed on row='{}', Exception={}", row, e.getMessage());
                    getReporter().write(TAG_NAME, "Could not process currency on row='{}'!", row + "");
                }
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

                    Polana1.this.close();
                    callback.onFailed(e);
                }

                @Override
                public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                    List<CurrencyData> result = Lists.newArrayList();

                    if (!isCanceled) {
                        try {
                            result = getPolana1(response.getEntity().getContent());
                        } catch (IOException | ParseException e) {
                            log.error("Could not parse source data!", e);
                            getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
                        }
                    } else {
                        log.warn("Request was canceled! No currencies were downloaded.");
                        getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
                    }

                    Polana1.this.close();
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
