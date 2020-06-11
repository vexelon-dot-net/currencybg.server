package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AltcoinsSource extends AbstractSource {

    private static final Logger log = LoggerFactory.getLogger(AltcoinsSource.class);
    private static final String TAG_NAME = AltcoinsSource.class.getSimpleName();

    private static final String URL_SOURCE = "https://altcoins.bg/currencies/rates";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    public static final Set<String> CURRENCIES =
            Sets.newHashSet(Defs.CURRENCY_BITCOIN, Defs.CURRENCY_ETHERIUM, Defs.CURRENCY_LITECOIN,
                    Defs.CURRENCY_RIPPLE, Defs.CURRENCY_BITCOIN_CASH, Defs.CURRENCY_DASH, Defs.CURRENCY_MONERO,
                    Defs.CURRENCY_ZCASH, Defs.CURRENCY_STELLAR, Defs.CURRENCY_EOS);

    public AltcoinsSource(Reporter reporter) {
        super(reporter);
    }

    /**
     * Transforms Altcoins json data into {@link CurrencyData} models.
     *
     * @return Parsed currencies.
     */
    public List<CurrencyData> getAltcoinsRates(InputStream input) throws IOException, ParseException {
        List<CurrencyData> result = Lists.newArrayList();

        try {
            String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

            Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

            String inputString = IOUtils.toString(input, Charsets.UTF_8.name());

            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<Map<String, List<String>>>() {
            }.getType();
            Map<String, List<String>> pairs = gson.fromJson(inputString, type);

            // {"BTC":[15273.172992,14557.243008],"ETH":[1347.9194792,1289.8877208],"LTC":[345.0662124,323.0193876],"XRP":[1.498323568,1.328702032],"DASH":[954.4866212,898.8854588],"XMR":[445.329248,411.073152],"ETC":[57.4290192,50.9276208],"ZEC":[634.818912,585.986688],"XLM":[0.5588930984,0.4956221816]}
            // {"BTC":[15231.025152,14517.070848],"ETH":[1344.894768,1286.993232],"LTC":[343.68943,321.73057],"XRP":[1.49815736,1.32855464],"DASH":[952.6273064,897.1344536],"XMR":[445.18656,410.94144],"ETC":[57.2025608,50.7267992],"ZEC":[635.043136,586.193664],"XLM":[0.5588494688,0.4955834912]}

            for (Map.Entry<String, List<String>> next : pairs.entrySet()) {
                if (CURRENCIES.contains(next.getKey()) && next.getValue().size() > 1) {
                    CurrencyData currencyData = new CurrencyData();
                    currencyData.setCode(next.getKey());
                    currencyData.setBuy(next.getValue().get(0));
                    currencyData.setSell(next.getValue().get(1));
                    currencyData.setRatio(1);
                    currencyData.setSource(Sources.ALTCOINS.getID());
                    currencyData.setDate(updateDate);
                    result.add(currencyData);
                } else if (log.isInfoEnabled()) {
                    log.info("Buy/sell not available or unexpected info present in '{}'! Size={}", next.getKey(), next.getValue().size());
                }
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

                    AltcoinsSource.this.close();
                    callback.onFailed(e);
                }

                @Override
                public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
                    List<CurrencyData> result = Lists.newArrayList();

                    if (!isCanceled) {
                        try {

                            result = getAltcoinsRates(response.getEntity().getContent());
                        } catch (IOException | ParseException e) {
                            log.error("Could not parse source data!", e);
                            getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e)
                            );
                        }
                    } else {
                        log.warn("Request was canceled! No currencies were downloaded.");
                        getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
                    }

                    AltcoinsSource.this.close();
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
