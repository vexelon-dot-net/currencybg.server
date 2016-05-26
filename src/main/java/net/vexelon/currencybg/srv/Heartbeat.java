package net.vexelon.currencybg.srv;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.DataSourceInterface;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.remote.Source;
import net.vexelon.currencybg.srv.remote.SourceException;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;

/**
 * Fetches currencies from remote server and imports them into the database.
 *
 */
public class Heartbeat implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Heartbeat.class);

	@Override
	public void run() {
		log.info("Downloading rates from sources ...");

		/*
		 * TODO: Fetch all sources from db
		 */
		List<Sources> sourcesList = Lists.newArrayList();
		sourcesList.add(Sources.TAVEX);

		/*
		 * Fetch currencies for every active source
		 */
		for (Sources sources : sourcesList) {
			try {
				// TODO: add proper reporter
				final ConsoleReporter reporter = new ConsoleReporter();
				sources.newInstance(reporter).getRates(new Source.Callback() {

					@Override
					public void onFailed(Exception e) {
						log.error("Source download failed!", e);

						if (!reporter.isEmpty()) {
							try {
								reporter.send();
							} catch (IOException ioe) {
								log.error("Failed sending report!", ioe);
							}
						}
					}

					@Override
					public void onCompleted(List<CurrencyData> currencyDataList) {
						log.debug("Source download succcesful.");

						// TODO Remove this map and use another
						Map<Integer, List<CurrencyData>> sourceToCurrencyMap = Maps.newHashMap();
						sourceToCurrencyMap.put(Sources.TAVEX.getID(), currencyDataList);

						if (log.isTraceEnabled()) {
							for (CurrencyData currency : currencyDataList) {
								log.trace(currency.toString());
							}
						}

						log.debug("Importing downloaded rates in database ...");
						try (final DataSourceInterface dataSource = new DataSource()) {
							dataSource.connect();
							// TODO: use another method without Map
							dataSource.addRates(sourceToCurrencyMap);
						} catch (IOException | DataSourceException e) {
							log.error("Could not connect to database!", e);
						}
					}
				});
			} catch (SourceException e) {
				log.error("Failed fetching rates from source!", e);
			}
		}

	}
}
