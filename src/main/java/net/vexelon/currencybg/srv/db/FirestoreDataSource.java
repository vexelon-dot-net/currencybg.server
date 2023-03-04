package net.vexelon.currencybg.srv.db;

import com.google.firebase.FirebaseApp;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.ReportData;

import java.io.IOException;
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
	public String getAllCurrentRatesAfter(Integer sourceId, Date timeFrom) throws DataSourceException {
		return null;
	}

	@Override
	public String getAllRates(Date dateFrom) throws DataSourceException {
		return null;
	}

	@Override
	public String getAllRates(Integer sourceId, Date dateFrom) throws DataSourceException {
		return null;
	}

	@Override
	public CurrencySource getSourceById(int id) throws DataSourceException {
		return null;
	}

	@Override
	public List<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException {
		 FirebaseApp.getInstance().g
	}

	@Override
	public boolean isCheckAuthentication(String authenticationKey) throws DataSourceException {
		return false;
	}

	@Override
	public void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException {

	}

	@Override
	public void addRates(List<CurrencyData> rates) throws DataSourceException {

	}

	@Override
	public void updateSource(int sourceId, CurrencySource source) throws DataSourceException {

	}

	@Override
	public void addReport(String message) throws DataSourceException {

	}

	@Override
	public List<ReportData> getReports() throws DataSourceException {
		return null;
	}

	@Override
	public void deleteReports(List<ReportData> reporters) throws DataSourceException {

	}

	@Override
	public void close() throws IOException {

	}
}
