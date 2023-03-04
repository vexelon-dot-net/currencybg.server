package net.vexelon.currencybg.srv.db;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.ReportData;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class FirestoreDataSource implements DataSource {

	FirestoreDataSource() {
	}

	@Override
	public void connect() throws DataSourceException {

	}

	@Override
	public String getAllCurrentRatesAfter(Date timeFrom) throws DataSourceException {
		return null;
	}

	@Override
	public String getAllCurrentRatesAfter(int sourceId, Date timeFrom) throws DataSourceException {
		return null;
	}

	@Override
	public String getAllRates(Date dateFrom) throws DataSourceException {
		return null;
	}

	@Override
	public String getAllRates(int sourceId, Date dateFrom) throws DataSourceException {
		return null;
	}

	@Override
	public Collection<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException {
		return null;
	}

	@Override
	public boolean isCheckAuthentication(String authenticationKey) throws DataSourceException {
		return false;
	}

	@Override
	public void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException {

	}

	@Override
	public void addRates(Collection<CurrencyData> rates) throws DataSourceException {

	}

	@Override
	public void updateSource(int sourceId, CurrencySource source) throws DataSourceException {

	}

	@Override
	public void addReportMessage(String message) throws DataSourceException {

	}

	@Override
	public Collection<ReportData> getReports() throws DataSourceException {
		return null;
	}

	@Override
	public void deleteReports(Collection<ReportData> reports) throws DataSourceException {

	}

	@Override
	public void close() throws IOException {

	}
}
