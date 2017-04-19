package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.MySQLDataSource;

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

		// TODO - test block
		try (final DataSource dataSource = new MySQLDataSource()) {
			dataSource.connect();
			dataSource.addReport(buffer.toString());
		} catch (IOException | DataSourceException ex) {
			log.error("Could not connect to database!", ex);
		}

	}

}
