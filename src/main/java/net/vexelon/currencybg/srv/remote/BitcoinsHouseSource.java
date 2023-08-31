package net.vexelon.currencybg.srv.remote;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @deprecated No longer operational.
 */
public class BitcoinsHouseSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(BitcoinsHouseSource.class);
	private static final String TAG_NAME = BitcoinsHouseSource.class.getSimpleName();

	private static final String URL_SOURCE  = "http://www.bitcoinshouse.com/chainexplorer/ajax/getLastPrice";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	@SuppressWarnings("unchecked")
	private static final List<Pair<String, String>> PAIRS = Lists.newArrayList(Pair.of("buy", "sell"),
			Pair.of("buyEthereum", "sellEthereum"), Pair.of("buyLitecoin", "sellLitecoin"),
			Pair.of("buyRipple", "sellRipple"), Pair.of("buyZcash", "sellZcash"));

	private static final Map<String, String> MAPPINGS = ImmutableMap.of("buy", Defs.CURRENCY_BITCOIN, "buyEthereum",
			Defs.CURRENCY_ETHERIUM, "buyLitecoin", Defs.CURRENCY_LITECOIN, "buyRipple", Defs.CURRENCY_RIPPLE,
			"buyZcash", Defs.CURRENCY_ZCASH);

	private String htmlData;

	public BitcoinsHouseSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms BitcoinsHouse BG HTML data into {@link CurrencyData} models.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<CurrencyData> getBitcoinsHouseRates(InputStream input) throws IOException, ParseException {
		List<CurrencyData> result = Lists.newArrayList();

		try {
			String currentDateTimeSofia = LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA))
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);
			String inputString = IOUtils.toString(input, StandardCharsets.UTF_8);

			Type type = new TypeToken<Map<String, String>>() {}.getType();
			Map<String, String> inputMap = new GsonBuilder().create().fromJson(inputString, type);

			for (Pair<String, String> pair : PAIRS) {
				// If some of the values buy or sell is 0, then we skip this currency
				if (!inputMap.get(pair.getLeft()).equals("0") && !inputMap.get(pair.getRight()).equals("0")) {
					CurrencyData currencyData = new CurrencyData();
					currencyData.setCode(MAPPINGS.get(pair.getLeft()));
					currencyData.setBuy(inputMap.get(pair.getLeft()));
					currencyData.setSell(inputMap.get(pair.getRight()));
					currencyData.setRatio(1);
					currencyData.setSource(Sources.BITCOINSHOUSE.getID());
					currencyData.setDate(updateDate);
					result.add(currencyData);
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

					BitcoinsHouseSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							// Skip the error report, if the server is down. Nothing to do, anyway.
							if ("text/html".equals(
									Optional.ofNullable(response.getFirstHeader(HttpHeaders.CONTENT_TYPE))
											.map(Header::getValue).orElse(""))) {
								log.warn("{} is down! Currencies fetch skipped.", TAG_NAME);
								callback.onCompleted(result);
								return;
							}

							result = getBitcoinsHouseRates(response.getEntity().getContent());
						} catch (IOException | ParseException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}  HTML= {}", ExceptionUtils.getStackTrace(e),
									htmlData);
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					BitcoinsHouseSource.this.close();
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
