package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.TrustAllX509Manager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSource implements Source {

	private static final Logger log = LoggerFactory.getLogger(AbstractSource.class);

	protected static final int DEFAULT_SOCKET_TIMEOUT  = 3 * 60 * 1000;
	protected static final int DEFAULT_CONNECT_TIMEOUT = 1 * 60 * 1000;

	private Reporter                 reporter;
	private CloseableHttpAsyncClient client;

	public AbstractSource(Reporter reporter) {
		this.reporter = reporter;
	}

	public void close() {
		IOUtils.closeQuietly(client);
	}

	/**
	 * Creates an asynchronous HTTP client configuration with default timeouts.
	 *
	 * @see #newHttpAsyncClient(boolean)
	 */
	protected static CloseableHttpAsyncClient newHttpAsyncClient(boolean useSSL) {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
				.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).build();

		HttpAsyncClientBuilder builder = HttpAsyncClients.custom();

		if (useSSL) {
			try {
				SSLContext context = SSLContext.getInstance("SSL");
				context.init(null, new TrustManager[] { new TrustAllX509Manager() }, new SecureRandom());

				SSLIOSessionStrategy strategy = new SSLIOSessionStrategy(context,
						SSLIOSessionStrategy.getDefaultHostnameVerifier());

				builder.setSSLStrategy(strategy);
			} catch (Exception e) {
				log.error("Failed initializing SSL context! Skipped.", e);
			}
		}

		return builder.setDefaultRequestConfig(requestConfig).build();
	}

	protected CloseableHttpAsyncClient getClient(boolean useSSL) {
		if (client == null) {
			client = newHttpAsyncClient(useSSL);
			client.start();
		}
		return client;
	}

	protected void doPost(URI uri, String entity, String contentType, final HTTPCallback httpCallback) {
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
		try {
			httpPost.setEntity(new StringEntity(entity));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		getClient(uri.getScheme().startsWith("https")).execute(httpPost, new FutureCallback<HttpResponse>() {

			@Override
			public void failed(Exception e) {
				httpCallback.onRequestFailed(e);
			}

			@Override
			public void completed(HttpResponse response) {
				httpCallback.onRequestCompleted(response, false);
			}

			@Override
			public void cancelled() {
				httpCallback.onRequestCompleted(null, false);
			}
		});
	}

	protected void doPost(String url, String entity, String contentType, final HTTPCallback responseCallback)
			throws URISyntaxException {
		doPost(new URI(url), entity, contentType, responseCallback);
	}

	protected void doGet(URI uri, String userAgent, final HTTPCallback httpCallback) {
		HttpGet httpGet = new HttpGet(uri);

		if (!StringUtils.isEmpty(userAgent)) {
			httpGet.setHeader("User-Agent", userAgent);
		}

		getClient(uri.getScheme().startsWith("https")).execute(httpGet, new FutureCallback<HttpResponse>() {

			@Override
			public void failed(Exception e) {
				httpCallback.onRequestFailed(e);
			}

			@Override
			public void completed(HttpResponse response) {
				httpCallback.onRequestCompleted(response, false);
			}

			@Override
			public void cancelled() {
				httpCallback.onRequestCompleted(null, false);
			}
		});
	}

	protected void doGet(URI uri, final HTTPCallback httpCallback) {
		doGet(uri, null, httpCallback);
	}

	protected void doGet(String url, final HTTPCallback responseCallback) throws URISyntaxException {
		doGet(new URI(url), responseCallback);
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
		List<CurrencyData> result = new ArrayList<CurrencyData>(currencyDataList);

		int i = 0;
		Iterator<CurrencyData> iterator = result.iterator();

		while (iterator.hasNext()) {
			CurrencyData currencyData = iterator.next();

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

	/**
	 * HTTP request callback
	 */
	public interface HTTPCallback {

		void onRequestCompleted(final HttpResponse response, boolean isCanceled);

		void onRequestFailed(Exception e);
	}

}
