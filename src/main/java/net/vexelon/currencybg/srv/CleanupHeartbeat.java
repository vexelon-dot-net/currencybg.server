package net.vexelon.currencybg.srv;

import net.vexelon.currencybg.srv.db.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deletes older currency rates from the database
 */
public class CleanupHeartbeat implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(CleanupHeartbeat.class);

	@Override
	public void run() {
		log.trace("Cleaning up currency rates ...");

		try (var dataSource = DataSource.newDataSource()) {
			dataSource.connect();
			dataSource.cleanupRates(GlobalConfig.INSTANCE.getCleanupInterval());
		} catch (Throwable t) {
			log.error("Fatal error during cleanup!", t);
		}
	}
}
