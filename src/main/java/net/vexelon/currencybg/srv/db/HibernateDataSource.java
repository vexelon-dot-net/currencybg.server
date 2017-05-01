package net.vexelon.currencybg.srv.db;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.CloseableSession;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.Currencies;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.ReportData;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import net.vexelon.currencybg.srv.utils.GsonDateTimeSerializer;
import net.vexelon.currencybg.srv.utils.HibernateUtil;

public class HibernateDataSource implements DataSource {

	private static final Logger log = LoggerFactory.getLogger(Currencies.class);

	private boolean isLogSql = false;

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Connection connect() throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllCurrentRatesAfter(Date timeFrom) throws DataSourceException {

		List<CurrencyData> currencies = Lists.newArrayList();
		currencies = getCurrentRatesAfter(null, timeFrom);

		Gson gson = new GsonBuilder().setDateFormat(Defs.DATEFORMAT_ISO_8601)
		        .registerTypeHierarchyAdapter(Date.class, new GsonDateTimeSerializer(Defs.DATETIME_TIMEZONE_SOFIA))
		        .create();
		Type type = new TypeToken<List<CurrencyData>>() {
		}.getType();

		return gson.toJson(currencies, type);

		// try (CloseableSession session = new
		// CloseableSession(HibernateUtil.getSessionFactory().openSession())) {
		// session.delegate().beginTransaction();
		//
		// APIKey apiKey = new APIKey();
		// apiKey.setKeyValue("YYYYYYYY");
		// apiKey.setStatus(2);
		//
		// session.delegate().save(apiKey);
		//
		// session.delegate().getTransaction().commit();
		//
		// return null;
		// }
	}

	private List<CurrencyData> getCurrentRatesAfter(Integer sourceId, Date timeFrom) throws DataSourceException {
		List<CurrencyData> currencies = Lists.newArrayList();

		try (CloseableSession session = new CloseableSession(HibernateUtil.getSessionFactory().openSession())) {
			session.delegate().beginTransaction();

			Date nextDay = DateTimeUtils.addDays(timeFrom, 1);

			SQLQuery query = session.delegate().createSQLQuery(
			        "SELECT code, ratio,  buy,  sell, date, source FROM cbg_currencies  WHERE date > (:today) AND date < (:nextDay)");
			query.setParameter("today", new Timestamp(timeFrom.getTime()));
			query.setParameter("nextDay", DateTimeUtils.toSqlDate(nextDay));

			List<CurrencyData> rows = query.list();

			// String sql = "SELECT c FROM cbg_currencies c WHERE SOURCE =
			// (:source) ";
			// TypedQuery<CurrencyData> query =
			// session.delegate().createQuery(sql);
			// query.setParameter("cource", 100);
			// List<CurrencyData> result = query.getResultList();

			// session.delegate().save(apiKey);
			//
			// session.delegate().getTransaction().commit();

		}
		return null;

		// PreparedStatement preparedStatement = null;
		// ResultSet rs = null;
		//
		// Date nextDay = DateTimeUtils.addDays(timeFrom, 1);
		//
		// String sqlSelect = " SELECT code, ratio, buy, sell, date, source FROM
		// cbg_currencies WHERE date > ? AND date < ? ";
		// if (sourceId != null) {
		// sqlSelect += "AND source = ? ORDER BY date asc";
		// } else {
		// sqlSelect += "ORDER BY date asc";
		// }
		//
		// if (log.isTraceEnabled() && isLogSql) {
		// log.trace("[SQL] {}", sqlSelect);
		// }
		//
		// try {
		// preparedStatement =
		// dbConnection.prepareStatement(sqlSelect.toString());
		// preparedStatement.setTimestamp(1, new Timestamp(timeFrom.getTime()));
		// preparedStatement.setDate(2, DateTimeUtils.toSqlDate(nextDay));
		// if (sourceId != null) {
		// preparedStatement.setInt(3, sourceId);
		// }
		//
		// rs = preparedStatement.executeQuery();
		//
		// while (rs.next()) {
		// currencies.add(new CurrencyData(rs.getString(1), rs.getInt(2),
		// rs.getString(3), rs.getString(4),
		// rs.getTimestamp(5), rs.getInt(6)));
		// }
		//
		// return currencies;
		//
		// } catch (SQLException e) {
		// throw new DataSourceException("SQL Exception in method
		// getCurrentRatesAfter!", e);
		//
		// } finally {
		// // TODO - close 2 PreprareStatement
		// if (rs != null) {
		// try {
		// rs.close();
		// } catch (SQLException e) {
		// log.error("Problem with close of ResultSet(for selectSQL) in method
		// getCurrentRatesAfter!", e);
		// }
		// }
		//
		// if (preparedStatement != null) {
		// try {
		// preparedStatement.close();
		// } catch (SQLException e) {
		// log.error("Problem with close of PreparedStatement(for selectSQL) in
		// method getCurrentRatesAfter!",
		// e);
		// }
		// }
		// }
	}

	@Override
	public String getAllCurrentRatesAfter(Integer sourceId, Date timeFrom) throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllRates(Date dateFrom) throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllRates(Integer sourceId, Date dateFrom) throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CurrencySource getSourceById(int id) throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCheckAuthentication(String authenticationKey) throws DataSourceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRates(List<CurrencyData> rates) throws DataSourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSource(int sourceId, CurrencySource source) throws DataSourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addReport(String message) throws DataSourceException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ReportData> getReports() throws DataSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteReports(List<ReportData> reporters) throws DataSourceException {
		// TODO Auto-generated method stub

	}

	// private final Session session;
	//
	// public HibernateDataSource(Session session) {
	// this.session = session;
	// }
	//
	// public Session delegate() {
	// return session;
	// }
	//
	// @Override
	// public void close() {
	// session.close();
	// }
}
