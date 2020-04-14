package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class XChangeBGSource extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(XChangeBGSource.class);
    private static final String TAG_NAME = XChangeBGSource.class.getSimpleName();

    private static final String URL = "https://xchange.bg";
    private static final String URL_SOURCE = "https://xchange.bg/bg/orders/estimate";
    private static final String HTTP_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String HTTP_REQUEST_BODY = "symbol=%s&amount=1&includeFees=0&size=amount&side=%s&csrf_name=%s&csrf_value=%s";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    private static final List<String> CURRENCIES = Lists.newArrayList("BTC-BGN", "ETH-BGN", "LTC-BGN", "BCH-BGN");
    private static final List<String> SIDES = Lists.newArrayList("buy", "sell");

    public XChangeBGSource(Reporter reporter) {
        super(reporter);
    }

    public static void main(String[] args) throws Exception {
        XChangeBGSource src = new XChangeBGSource(new ConsoleReporter());
        src.getRates(new Callback() {
            @Override public void onCompleted(List<CurrencyData> currencyDataList) {
                for (CurrencyData cd : currencyDataList) {
                    System.out.println(cd.toString());
                }
            }

            @Override public void onFailed(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Transforms xChange BG JSON data streams into {@link CurrencyData} model.
     */
    public List<CurrencyData> getXChangeRates(InputStream buyInput, InputStream sellInput, String currency)
            throws IOException, ParseException {
        try {
            String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT));

            Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            Map<String, String> buy = gson.fromJson(IOUtils.toString(buyInput, StandardCharsets.UTF_8), type);
            Map<String, String> sell = gson.fromJson(IOUtils.toString(sellInput, StandardCharsets.UTF_8), type);

            CurrencyData currencyData = new CurrencyData();
            currencyData.setCode(currency.substring(0, currency.indexOf("-")));
            currencyData.setBuy(buy.get("cost"));
            currencyData.setSell(sell.get("cost"));
            currencyData.setRatio(Float.valueOf(buy.get("amount")).intValue());
            currencyData.setSource(Sources.XCHANGE.getID());
            currencyData.setDate(updateDate);

            return normalizeCurrencyData(Collections.singletonList(currencyData));
        } catch (RuntimeException e) {
            throw new IOException(e);
        }
    }

    public List<String> getXChangeCSRF(InputStream input) throws IOException {
        String csrfName = "", csrfValue = "";

        Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), URL);
        Elements metaTags = doc.getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            String name = metaTag.attr("name");
            if ("csrf_name".equals(name)) {
                csrfName = metaTag.attr("content");
            } else if ("csrf_value".equals(name)) {
                csrfValue = metaTag.attr("content");
            }
        }

        return Lists.newArrayList(csrfName, csrfValue);
    }

    @Override
    public void getRates(Callback callback) throws SourceException {
        List<String> csrf = new ArrayList<>();
        List<CurrencyData> result = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        try {
            doGet(URL, new HTTPCallback() {

                @Override
                public void onRequestFailed(Exception e) {
                    getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

                    XChangeBGSource.this.close();
                    callback.onFailed(e);

                    latch.countDown();
                }

                @Override
                public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                    if (!isCanceled) {
                        try {
                            csrf.addAll(getXChangeCSRF(response.getEntity().getContent()));
                            if (csrf.size() != 2) {
                                throw new RuntimeException("Invalid csrf values fetched! Size=" + csrf.size());
                            }
                        } catch (Exception e) {
                            getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));
                            XChangeBGSource.this.close();
                            callback.onFailed(e);
                        }
                    } else {
                        log.warn("Request was canceled! No currencies were downloaded.");
                        getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
                    }

                    latch.countDown();
                }
            });

            // wait till csrf values are fetched
            latch.await();

        } catch (URISyntaxException e) {
            throw new SourceException("Invalid source url - " + URL_SOURCE, e);
        } catch (Throwable e) {
            throw new SourceException("Unexpected error! ", e);
        }

        if (!csrf.isEmpty()) {
            for (String currency : CURRENCIES) {
                Pair<List<byte[]>, List<Exception>> pair = getFor(currency, csrf.get(0), csrf.get(1));

                // at least one request has failed
                if (!pair.getRight().isEmpty()) {
                    XChangeBGSource.this.close();
                    callback.onFailed(pair.getRight().iterator().next());
                    return;
                }

                // make sure both buy and sell requests were successful
                if (pair.getLeft().size() > 1) {
                    try (ByteArrayInputStream buyInput = new ByteArrayInputStream(pair.getLeft().get(0));
                         ByteArrayInputStream sellInput = new ByteArrayInputStream(pair.getLeft().get(1))) {
                        result.addAll(getXChangeRates(buyInput, sellInput, currency));
                    } catch (IOException | ParseException e) {
                        log.error("Could not parse source '{}' data!", currency, e);
                        getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }

        XChangeBGSource.this.close();
        callback.onCompleted(result);
    }

    @Override
    public String getName() {
        return TAG_NAME;
    }

    private Pair<List<byte[]>, List<Exception>> getFor(String currency, String csrfName, String csrfValue) throws SourceException {
        final Pair<List<byte[]>, List<Exception>> result = Pair.of(new ArrayList<>(), new ArrayList<>());
        final CountDownLatch latch = new CountDownLatch(SIDES.size());

        try {
            for (String side : SIDES) {
                doPost(URL_SOURCE, String.format(HTTP_REQUEST_BODY, currency, side, csrfName, csrfValue),
                        HTTP_CONTENT_TYPE, new HTTPCallback() {

                            @Override
                            public void onRequestFailed(Exception e) {
                                getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));
                                result.getRight().add(e);
                                // one down
                                latch.countDown();
                            }

                            @Override
                            public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                                if (!isCanceled) {
                                    if (response.getStatusLine().getStatusCode() / 100 != 2) {
                                        log.error("{} download error! (Code={}) {}", currency, response.getStatusLine().getStatusCode(),
                                                response.getStatusLine().getReasonPhrase());
                                    } else {
                                        try {
                                            result.getLeft().add(IOUtils.toByteArray(response.getEntity().getContent()));
                                        } catch (IOException e) {
                                            log.error("Could not extract response data!", e);
                                            getReporter().write(TAG_NAME, "Response data failed= {} ", ExceptionUtils.getStackTrace(e));
                                            // it counts as a failed request
                                            result.getRight().add(e);
                                        }
                                    }
                                } else {
                                    log.warn("Request was canceled! No currencies were downloaded.");
                                    getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
                                }
                                // one down
                                latch.countDown();
                            }
                        });
            }

            // wait until both buy & sell requests complete or fail!
            latch.await();

            return result;
        } catch (URISyntaxException e) {
            throw new SourceException("Invalid source url - " + URL_SOURCE, e);
        } catch (Throwable e) {
            throw new SourceException("Unexpected error! ", e);
        }
    }
}
