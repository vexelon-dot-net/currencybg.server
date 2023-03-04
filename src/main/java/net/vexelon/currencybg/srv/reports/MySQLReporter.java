package net.vexelon.currencybg.srv.reports;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MySQLReporter extends AbstractReporter {

	private static final Logger log = LoggerFactory.getLogger(MySQLReporter.class);

	public MySQLReporter(String name) {
		super(name);
	}

	public MySQLReporter() {
		this("MySQL");
	}

	@Override
	public void send() throws IOException {
		try (final var dataSource = DataSource.newDataSource()) {
			dataSource.connect();
			dataSource.addReportMessage(buffer.toString());
		} catch (IOException | DataSourceException ex) {
			log.error("Could not connect to database!", ex);
		}
	}

}
