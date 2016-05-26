package net.vexelon.currencybg.srv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.DataSourceInterface;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.remote.Source;
import net.vexelon.currencybg.srv.remote.SourceException;
import net.vexelon.currencybg.srv.remote.TavexSource;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;

/**
 * Fetches currencies from remote server and imports them into the database.
 *
 */
public class Heartbeat implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Heartbeat.class);

	@Override
	public void run() {
		log.info("Updating rates from remote server ...");

		/*
		 * Fetch currencies for every active source
		 */
		try (final DataSourceInterface dataSource = new DataSource()) {
			final ConsoleReporter reporter = new ConsoleReporter();
			new TavexSource(reporter).getRates(new Source.Callback() {

				@Override
				public void onFailed() {
					if (!reporter.isEmpty()) {
						try {
							reporter.send();
						} catch (IOException e) {
							log.error("Failed sending report!", e);
						}
					}
				}

				@Override
				public void onCompleted(List<CurrencyData> currencyDataList) {
					// TODO Remove this map and use another
					Map<Integer, List<CurrencyData>> downloadRates = Maps.newHashMap();
					downloadRates.put(Sources.Tavex.getID(), currencyDataList);

					if (log.isTraceEnabled()) {
						// TODO: remove info
						for (Map.Entry<Integer, List<CurrencyData>> rates : downloadRates.entrySet()) {
							log.trace("*** downloaded locale: {}", rates.getKey());
							for (CurrencyData currency : rates.getValue()) {
								// log.trace("Currency: {} ({}) = {}",
								// currency.getName(), currency.getCode(),
								// currency.getRate());
							}
						}
					}

					log.debug("Importing downloaded rates in database ...");
					try {
						dataSource.connect();
						// TODO: use another method without Map
						dataSource.addRates(downloadRates);
					} catch (DataSourceException e) {
						log.error("Could not connect to database!", e);
					}
				}
			});

		} catch (SourceException e) {
			log.error("Could not download currencies from remote!", e);
		} catch (IOException e) {
			log.error("Could not connect to database!", e);
		}
	}
}
