package net.vexelon.currencybg.srv.remote;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import net.vexelon.currencybg.srv.utils.TrustAllX509Manager;

public abstract class AbstractSource implements Source {

	private static final Logger log = LoggerFactory.getLogger(AbstractSource.class);

	protected static final int DEFAULT_SOCKET_TIMEOUT = 3 * 60 * 1000;
	protected static final int DEFAULT_CONNECT_TIMEOUT = 1 * 60 * 1000;

	private Reporter reporter;
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
	 * @param useSSL
	 * @see #newHttpClient()
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

	protected void doGet(URI uri, final HTTPCallback httpCallback) {
		HttpGet httpGet = new HttpGet(uri);
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

	protected void doGet(String url, final HTTPCallback responseCallback) throws URISyntaxException {
		doGet(new URI(url), responseCallback);
	}

	/**
	 * Verifies that each {@link CurrencyData} entry contains a valid or
	 * meaningful value. One or more currencies may be removed, if their models
	 * do not contain a currency code or {@link Sources}.
	 * 
	 * @param currencyDataList
	 *            Immutable currencies list.
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

				if (currencyData.getRatio() < 0) {
					getReporter().write(getName(), tag + " - invalid ratio. Will use 0.");
					currencyData.setRatio(0);
				}

				if (!currencyData.getBuy().isEmpty()) {
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
					try {
						new BigDecimal(currencyData.getSell());
					} catch (NumberFormatException e) {
						// log.warn(tag + " - cannot parse Sell value=" +
						// StringUtils.defaultString(currencyData.getSell()));
						getReporter().write(getName(), tag + " - cannot parse Sell value="
						        + StringUtils.defaultString(currencyData.getSell()));

						// set default
						currencyData.setSell("");
					}
				}
			} catch (SourceException e) {
				log.warn("Currency entry normalization error!", e.getMessage());
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
	 *
	 */
	public interface HTTPCallback {

		void onRequestCompleted(final HttpResponse response, boolean isCanceled);

		void onRequestFailed(Exception e);
	}

}
