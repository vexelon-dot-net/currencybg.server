package net.vexelon.currencybg.srv;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.DataSourceInterface;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
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
		try {
			/*
			 * Fetch all (active) sources from database
			 */
			List<CurrencySource> allSources = Lists.newArrayList();
			try (final DataSourceInterface dataSource = new DataSource()) {
				dataSource.connect();
				// TODO active param boolean
				allSources = dataSource.getAllSources();
			} catch (IOException | DataSourceException e) {
				log.error("Could not connect to database!", e);
			}

			/*
			 * Fetch currencies for every active source
			 */
			for (CurrencySource currencySource : allSources) {
				try {
					final Sources sourceType = Sources.valueOf(currencySource.getSourceId());
					if (sourceType != null) {
						// TODO: add proper reporter
						final ConsoleReporter reporter = new ConsoleReporter();
						final Source source = sourceType.newInstance(reporter);
						// TODO: set update flag in db
						source.getRates(new Source.Callback() {

							@Override
							public void onFailed(Exception e) {
								log.error("{} - source download failed!", source.getName(), e);
								if (!reporter.isEmpty()) {
									try {
										reporter.send();
									} catch (IOException ioe) {
										log.error("{} - Failed sending report!", source.getName(), ioe);
									}
								}
							}

							@Override
							public void onCompleted(List<CurrencyData> currencyDataList) {
								log.debug("{} - source download succcesful.", source.getName());

								if (log.isTraceEnabled()) {
									// TODO remove this trace log
									for (CurrencyData currency : currencyDataList) {
										log.trace(currency.toString());
									}
								}

								log.debug("{} - importing downloaded rates in database ...", source.getName());
								try (final DataSourceInterface dataSource = new DataSource()) {
									dataSource.connect();
									dataSource.addRates(sourceType.getID(), currencyDataList);
								} catch (IOException | DataSourceException e) {
									log.error("Could not connect to database!", e);
								}
							}
						});
					}
				} catch (SourceException e) {
					log.error("Failed fetching rates for source id='{}'!", currencySource.getSourceId(), e);
				}
			}
		} catch (Throwable t) {
			/*
			 * The executor swallows exceptions, so catch Throwable instead.
			 * 
			 * @see http://stackoverflow.com/a/24902026
			 */
			log.error("Fatal hearbeat error!", t);
		}
	}
}
