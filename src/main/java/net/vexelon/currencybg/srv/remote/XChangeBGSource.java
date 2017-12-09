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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class XChangeBGSource extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(XChangeBGSource.class);
    private static final String TAG_NAME = XChangeBGSource.class.getSimpleName();

    private static final String URL_SOURCE = "https://www.xchange.bg/en";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    public XChangeBGSource(Reporter reporter) {
        super(reporter);
    }

    /**
     * Transforms xChange BG HTML data into {@link CurrencyData} models.
     *
     * @param input
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public List<CurrencyData> getXChangeRates(InputStream input) throws IOException, ParseException {
        List<CurrencyData> result = Lists.newArrayList();

        Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL_SOURCE);

        try {
            String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

            Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

            // Parse table with currencies
            Element buyElement = doc.select("li.bitcoin-value > a.buy-button-small > div.hidden-sm").get(0);
            String buy = sanitizeValue(buyElement.text());

            Element sellElement = doc.select("li.bitcoin-value > a.sell-button-small > div.hidden-sm").get(0);
            String sell = sanitizeValue(sellElement.text());

            CurrencyData currencyData = new CurrencyData();
            currencyData.setCode(Defs.CURRENCY_BITCOIN);
            currencyData.setBuy(buy);
            currencyData.setSell(sell);
            currencyData.setRatio(1);
            currencyData.setSource(Sources.XCHANGE.getID());
            currencyData.setDate(updateDate);
            result.add(currencyData);

            return normalizeCurrencyData(result);
        } catch (RuntimeException e) {
            throw new IOException(e);
        }
    }

    private String sanitizeValue(String value) {
        String result = StringUtils.trimToEmpty(value.replaceAll("\\P{Print}", ""));
        result = StringUtils.removeStart(result, ":");
        result = StringUtils.removeEnd(result, ".");
        return StringUtils.trimToEmpty(result);
    }

    @Override
    public void getRates(Callback callback) throws SourceException {
        try {
            doGet(URL_SOURCE, new HTTPCallback() {

                @Override
                public void onRequestFailed(Exception e) {
                    getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

                    XChangeBGSource.this.close();
                    callback.onFailed(e);
                }

                @Override
                public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                    List<CurrencyData> result = Lists.newArrayList();

                    if (!isCanceled) {
                        try {

                            result = getXChangeRates(response.getEntity().getContent());
                        } catch (IOException | ParseException e) {
                            log.error("Could not parse source data!", e);
                            getReporter().write(TAG_NAME, "Parse failed= {} ", ExceptionUtils.getStackTrace(e));
                        }
                    } else {
                        log.warn("Request was canceled! No currencies were downloaded.");
                        getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
                    }

                    XChangeBGSource.this.close();
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
