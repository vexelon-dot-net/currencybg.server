package net.vexelon.currencybg.srv.remote;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.ClientOptionsBase;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.UserAgentUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractSource implements Source {

	private static final Logger log = LoggerFactory.getLogger(AbstractSource.class);

	protected static final int DEFAULT_SOCKET_TIMEOUT  = 3 * 60 * 1000;
	protected static final int DEFAULT_CONNECT_TIMEOUT = 1 * 60 * 1000;

	private final Vertx     vertx;
	private final Reporter  reporter;
	private       WebClient client;


	public AbstractSource(Vertx vertx, Reporter reporter) {
		this.vertx = vertx;
		this.reporter = reporter;
	}

	//	public AbstractSource(Reporter reporter) {
	//		this.reporter = reporter;
	//	}

	public void close() {
		if (client != null) {
			client.close();
		}
	}

	/**
	 * Creates an HTTP client configuration with default timeouts, if it does not already exist
	 */
	protected WebClient getClient(boolean useSSL) {
		if (client == null) {
			client = WebClient.create(vertx, new WebClientOptions()
					// follow 301 redirects
					.setFollowRedirects(true)
					// drop connection timeout
					.setConnectTimeout(ClientOptionsBase.DEFAULT_CONNECT_TIMEOUT)
					// trust all server certs
					.setTrustAll(true)
					// use h2
					.setProtocolVersion(HttpVersion.HTTP_2).setUseAlpn(true));
		}

		return client;
	}

	private HttpResponseWrapper toResponseWrapper(HttpResponse<Buffer> response) {
		return new HttpResponseWrapper(response.body().getBytes(), response.headers().entries().stream().collect(
				Collectors.groupingBy(Map.Entry::getKey,
						Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));
	}

	protected void doPost(String url, String entity, String contentType, final HTTPCallback httpCallback) {
		getClient(url.startsWith("https")).postAbs(url)
				// max timeout
				.timeout(DEFAULT_SOCKET_TIMEOUT)
				// user-agent string
				.putHeader(HttpHeaders.USER_AGENT.toString(), UserAgentUtils.random())
				// post content-type
				.putHeader(HttpHeaders.CONTENT_TYPE.toString(), contentType)
				// post body
				.sendBuffer(Buffer.buffer(entity))
				// ok
				.onSuccess(response -> httpCallback.onRequestCompleted(toResponseWrapper(response), false))
				// failed
				.onFailure(httpCallback::onRequestFailed);
	}

	protected void doGet(String url, String userAgent, final HTTPCallback httpCallback) {
		getClient(url.startsWith("https")).getAbs(url)
				// max timeout
				.timeout(DEFAULT_SOCKET_TIMEOUT)
				// user-agent string
				.putHeader(HttpHeaders.USER_AGENT.toString(),
						Objects.toString(userAgent).isBlank() ? UserAgentUtils.random() : userAgent)
				// send request
				.send()
				// ok
				.onSuccess(response -> httpCallback.onRequestCompleted(toResponseWrapper(response), false))
				// failed
				.onFailure(httpCallback::onRequestFailed);
	}

	protected void doGet(String url, final HTTPCallback responseCallback) {
		doGet(url, null, responseCallback);
	}

	/**
	 * Verifies that each {@link CurrencyData} entry contains a valid or
	 * meaningful value. One or more currencies may be removed, if their models
	 * do not contain a currency code or {@link Sources}.
	 *
	 * @param currencyDataList Immutable currencies list.
	 * @return Normalized list of {@link CurrencyData} models.
	 */
	protected List<CurrencyData> normalizeCurrencyData(final List<CurrencyData> currencyDataList) {
		var result = new ArrayList<CurrencyData>(currencyDataList);

		int i = 0;
		var iterator = result.iterator();

		while (iterator.hasNext()) {
			var currencyData = iterator.next();

			try {
				if (StringUtils.isEmpty(currencyData.getCode())) {
					throw new SourceException(i + " - missing currency code!");
				}
				String tag = "(" + i + ") " + currencyData.getCode();

				if (currencyData.getSource() == 0) {
					throw new SourceException(tag + " - source cannot be '0' for currency=" + currencyData.getCode());
				}

				switch (Sources.valueOf(currencyData.getSource())) {
					case BNB:
					case FIB:
					case TAVEX:
					case POLANA1:
					case FACTORIN:
					case UNICREDIT:
					case SGEB:
					case CHANGEPARTNER:
					case FOREXHOUSE:
					case ALLIANZ:
						if (currencyData.getCode().length() > 3) {
							log.warn("Invalid currency code for {}! Skipped entry: {}", tag, currencyData.toString());
							iterator.remove();
							continue;
						}
						break;
				}

				// Check for obsolete currency code
				switch (currencyData.getCode()) {
					case "TRL":
						currencyData.setCode("TRY");
						break;
					case "BYR":
						currencyData.setCode("BYN");
						break;
					case "RUR":
						currencyData.setCode("RUB");
						break;
					case "CSD":
						currencyData.setCode("RSD");
						break;
					default:
						break;
				}

				if (currencyData.getRatio() < 0) {
					getReporter().write(getName(), tag + " - invalid ratio. Will use 0.");
					currencyData.setRatio(0);
				}

				if (!currencyData.getBuy().isEmpty()) {
					String buy = currencyData.getBuy();

					// Fixes "cannot parse Buy value=0..465"
					buy = buy.replace("..", ".");
					// Fixes "cannot parse Buy value=1,655"
					buy = buy.replace(",", ".");
					// column max size is 10 chars
					//					buy = StringUtils.left(buy, 10);

					currencyData.setBuy(buy);

					try {
						new BigDecimal(currencyData.getBuy());
					} catch (NumberFormatException e) {
						// log.warn(tag + " - cannot parse Buy value=" +
						// StringUtils.defaultString(currencyData.getBuy()));
						getReporter().write(getName(),
								tag + " - cannot parse Buy value=" + StringUtils.defaultString(currencyData.getBuy()));

						// set default
						currencyData.setBuy("");
					}
				}

				if (!currencyData.getSell().isEmpty()) {
					String sell = currencyData.getSell();

					// Fixes "cannot parse Sell value=0..465"
					sell = sell.replace("..", ".");
					// Fixes "cannot parse Sell value=1,655"
					sell = sell.replace(",", ".");
					// column max site is 10 chars
					//					sell = StringUtils.left(sell, 10);

					currencyData.setSell(sell);

					try {
						new BigDecimal(currencyData.getSell());
					} catch (NumberFormatException e) {
						// log.warn(tag + " - cannot parse Sell value=" +
						// StringUtils.defaultString(currencyData.getSell()));
						getReporter().write(getName(), tag + " - cannot parse Sell value=" + StringUtils.defaultString(
								currencyData.getSell()));

						// set default
						currencyData.setSell("");
					}
				}

				if (StringUtils.isBlank(currencyData.getBuy()) && StringUtils.isBlank(currencyData.getSell())) {
					log.warn("{} - both sell and buy values are empty for currency={}. Skipped.", tag,
							currencyData.getCode());
					iterator.remove();
					//                    throw new SourceException(tag + " - both sell and buy values are empty for currency=" + currencyData.getCode());
				}
			} catch (SourceException e) {
				log.warn("Currency entry normalization error! {}", e.getMessage());
				getReporter().write(getName(), e.getMessage());
				getReporter().write(getName(),
						"Removing currency data entry for - " + StringUtils.defaultString(currencyData.getCode()));
				iterator.remove();
			}

			i += 1;
		}

		return result;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public record HttpResponseWrapper(byte[] content, Map<String, List<String>> headers) {}

	/**
	 * HTTP request callback
	 */
	public interface HTTPCallback {

		void onRequestCompleted(HttpResponseWrapper response, boolean isCanceled);

		void onRequestFailed(Throwable t);
	}
}
