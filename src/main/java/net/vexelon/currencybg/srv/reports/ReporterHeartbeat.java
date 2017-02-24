package net.vexelon.currencybg.srv.reports;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.MySQLDataSource;
import net.vexelon.currencybg.srv.db.models.ReportData;
import net.vexelon.currencybg.srv.remote.BNBSource;

public class ReporterHeartbeat implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ReporterHeartbeat.class);

	@Override
	public void run() {
		try (final DataSource dataSource = new MySQLDataSource()) {

			dataSource.connect();
			List<ReportData> reports = dataSource.getReports();

			if (!reports.isEmpty()) {
				SparkPostReporter reporter = new SparkPostReporter();
				reporter.write(BNBSource.class.getSimpleName(), "EORRRRR");
				reporter.send();

			}

		} catch (IOException | DataSourceException e) {
			log.error("Could not connect to database!", e);
		}
	}

}
