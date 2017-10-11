package net.vexelon.currencybg.srv.remote;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class BitcoinsHouseSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(BitcoinsHouseSource.class);
	private static final String TAG_NAME = BitcoinsHouseSource.class.getSimpleName();

	private static final String URL_SOURCE = "http://www.bitcoinshouse.com/chainexplorer/ajax/getLastPrice";
	private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

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
			        .format(DateTimeFormatter.ofPattern(DATE_FORMAT)).toString();

			Date updateDate = DateTimeUtils.parseDate(currentDateTimeSofia, DATE_FORMAT);

			@SuppressWarnings("unchecked")
			List<Pair<String, String>> pairType = Lists.newArrayList(Pair.of("buy", "sell"),
			        Pair.of("buyEthereum", "sellEthereum"), Pair.of("buyLitecoin", "sellLitecoin"),
			        Pair.of("buyRipple", "sellRipple"), Pair.of("buyZcash", "sellZcash"));

			Map<String, String> keymap = ImmutableMap.of("buy", Defs.CURRENCY_BITCOIN, "buyEthereum",
			        Defs.CURRENCY_ETHERIUM, "buyLitecoin", Defs.CURRENCY_LITECOIN, "buyRipple", Defs.CURRENCY_RIPPLE,
			        "buyZcash", Defs.CURRENCY_ZCASH);

			String inputString = IOUtils.toString(input, "UTF-8");

			Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			Map<String, String> inputMap = gson.fromJson(inputString, type);

			CurrencyData currencyData;
			for (Pair<String, String> pair : pairType) {
				// If some of the values buy or sell is 0, then we skip this
				// currency
				if (Double.parseDouble(inputMap.get(pair.getLeft())) != (double) 0
				        && Double.parseDouble(inputMap.get(pair.getRight())) != (double) 0) {
					currencyData = new CurrencyData();
					currencyData.setCode(keymap.get(pair.getLeft()));
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
