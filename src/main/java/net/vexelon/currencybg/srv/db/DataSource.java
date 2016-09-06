package net.vexelon.currencybg.srv.db;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.Currencies;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.SourceUpdateRestrictions;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class DataSource implements DataSourceInterface {

	private static final Logger log = LoggerFactory.getLogger(Currencies.class);

	private Connection dbConnection = null;

	@Override
	public Connection connect() throws DataSourceException {
		try {
			Class.forName(Defs.DB_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new DataSourceException("Could not find DB driver!", e);
		}

		try {
			dbConnection = DriverManager.getConnection(Defs.DB_CONNECTION, Defs.DB_USER, Defs.DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			throw new DataSourceException("Could not open SQLite database!", e);

		}
	}

	@Override
	public void close() {
		if (dbConnection != null) {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				log.error("Could not close database connection!", e);
			}
		}
	}

	@Override
	public void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException {

		for (Map.Entry<Integer, List<CurrencyData>> currenciesData : rates.entrySet()) {

			List<CurrencyData> currencies = new ArrayList<CurrencyData>();
			for (CurrencyData currency : currenciesData.getValue()) {
				currencies.add(currency);
			}

			// make sure there is sufficient data in the lists
			if (currencies.isEmpty() || currencies.size() < 2) {
				log.debug("Dynamic currencies list is empty (" + currencies.size() + ")!");
				return;
			}

			PreparedStatement preparedStatement = null;
			String insertSQL = "INSERT INTO cbg_currencies (CODE, RATIO, BUY, SELL, DATE, SOURCE) VALUES (?,?,?,?,?,?)";

			try {
				preparedStatement = dbConnection.prepareStatement(insertSQL);

				for (int i = 0; i < currencies.size(); i++) {

					preparedStatement.setString(1, currencies.get(i).getCode());
					preparedStatement.setInt(2, currencies.get(i).getRatio());
					preparedStatement.setString(3, currencies.get(i).getBuy());
					preparedStatement.setString(4, currencies.get(i).getSell());
					preparedStatement.setTimestamp(5, new java.sql.Timestamp(currencies.get(i).getDate().getTime()));
					preparedStatement.setInt(6, currencies.get(i).getSource());

					preparedStatement.executeUpdate();

				}

			} catch (SQLException e) {
				throw new DataSourceException("SQL Exception in method addRates!", e);
			} finally {

				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						log.error("Problem with close of PreparedStatement in method addRates!", e);
					}
				}

			}

		}
	}

	@Override
	public void addRates(List<CurrencyData> currencies) throws DataSourceException {

		if (!currencies.isEmpty() && currencies != null) {

			PreparedStatement preparedStatement = null;
			String insertSQL = "INSERT INTO cbg_currencies (CODE, RATIO, BUY, SELL, DATE, SOURCE) VALUES (?,?,?,?,?,?)";

			try {
				preparedStatement = dbConnection.prepareStatement(insertSQL);

				for (int i = 0; i < currencies.size(); i++) {

					preparedStatement.setString(1, currencies.get(i).getCode());
					preparedStatement.setInt(2, currencies.get(i).getRatio());
					preparedStatement.setString(3, currencies.get(i).getBuy());
					preparedStatement.setString(4, currencies.get(i).getSell());
					preparedStatement.setTimestamp(5, new java.sql.Timestamp(currencies.get(i).getDate().getTime()));
					preparedStatement.setInt(6, currencies.get(i).getSource());

					preparedStatement.executeUpdate();

				}

			} catch (SQLException e) {
				throw new DataSourceException("SQL Exception in method addRates!", e);
			} finally {

				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						log.error("Problem with close of PreparedStatement in method addRates!", e);
					}
				}

			}
		}

	}

	@Override
	public void updateSource(int sourceId, CurrencySource source) throws DataSourceException {
		PreparedStatement preparedStatement = null;
		String insertSQL = "UPDATE cbg_sources SET NAME=COALESCE(?, NAME), STATUS=COALESCE(?, STATUS), UPDATE_PERIOD=COALESCE(?, UPDATE_PERIOD), LAST_UPDATE=COALESCE(?, LAST_UPDATE) WHERE SOURCE_ID = ?";

		try {
			preparedStatement = dbConnection.prepareStatement(insertSQL);

			if (source.getSourceName() != null) {
				preparedStatement.setString(1, source.getSourceName());
			} else {
				preparedStatement.setNull(1, java.sql.Types.VARCHAR);
			}

			if (source.getStatus() != 0) {
				preparedStatement.setInt(2, source.getStatus());
			} else {
				preparedStatement.setNull(2, java.sql.Types.INTEGER);
			}

			if (source.getUpdatePeriod() != 0) {
				preparedStatement.setInt(3, source.getUpdatePeriod());
			} else {
				preparedStatement.setNull(3, java.sql.Types.INTEGER);
			}

			if (source.getLastUpdate() != null) {
				preparedStatement.setTimestamp(4, new java.sql.Timestamp(source.getLastUpdate().getTime()));
			} else {
				preparedStatement.setNull(4, java.sql.Types.DATE);
			}

			preparedStatement.setInt(5, sourceId);

			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw new DataSourceException("SQL Exception in method addRates!", e);
		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement in method addRates!", e);
				}
			}

		}
	}

	@Override
	public boolean isCheckAuthentication(String authenticationKey) throws DataSourceException {

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		String selectSQL = "SELECT 1 FROM cbg_apikeys WHERE key_value = ? AND status = 0 ";
		try {
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setString(1, authenticationKey);
			rs = preparedStatement.executeQuery();

			if (rs.next())
				return true;
		} catch (SQLException e) {
			throw new DataSourceException("SQL Exception in method getFixedRates!", e);
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet in method isCheckAuthentication!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement in method isCheckAuthentication!", e);
				}
			}

		}

		return false;
	}

	@Override
	public String getAllCurrentRatesAfter(Date timeFrom) throws DataSourceException {
		List<CurrencyData> currencies = Lists.newArrayList();
		currencies = getCurrentRatesAfter(null, timeFrom);

		Gson gson = new GsonBuilder().setDateFormat(Defs.DATEFORMAT_ISO_8601).create();
		Type type = new TypeToken<List<CurrencyData>>() {
		}.getType();

		return gson.toJson(currencies, type);
	}

	@Override
	public String getAllCurrentRatesAfter(Integer sourceId, Date timeFrom) throws DataSourceException {

		List<CurrencyData> currencies = Lists.newArrayList();
		currencies = getCurrentRatesAfter(sourceId, timeFrom);

		Gson gson = new GsonBuilder().setDateFormat(Defs.DATEFORMAT_ISO_8601).create();
		Type type = new TypeToken<List<CurrencyData>>() {
		}.getType();

		return gson.toJson(currencies, type);
	}

	private List<CurrencyData> getCurrentRatesAfter(Integer sourceId, Date timeFrom) throws DataSourceException {
		List<CurrencyData> currencies = Lists.newArrayList();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		Date nextDay = DateTimeUtils.addDays(timeFrom, 1);

		String sqlSelect = " SELECT code, ratio,  buy,  sell, date, source FROM cbg_currencies  WHERE date > ? AND date < ? ";
		if (sourceId != null) {
			sqlSelect += "AND source = ? ORDER BY date asc";
		} else {
			sqlSelect += "ORDER BY date asc";
		}

		log.trace("Selected rows {} in {}", sqlSelect, sqlSelect);

		try {
			preparedStatement = dbConnection.prepareStatement(sqlSelect.toString());
			preparedStatement.setTimestamp(1, new Timestamp(timeFrom.getTime()));
			preparedStatement.setDate(2, DateTimeUtils.toSqlDate(nextDay));
			if (sourceId != null) {
				preparedStatement.setInt(3, sourceId);
			}

			rs = preparedStatement.executeQuery();

			while (rs.next()) {
				currencies.add(new CurrencyData(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4),
						rs.getTimestamp(5), rs.getInt(6)));
			}

			return currencies;

		} catch (SQLException e) {
			throw new DataSourceException("SQL Exception in method getCurrentRatesAfter!", e);

		} finally {
			// TODO - close 2 PreprareStatement
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet(for selectSQL) in method getCurrentRatesAfter!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement(for selectSQL) in method getCurrentRatesAfter!",
							e);
				}
			}

		}

	}

	@Override
	public String getAllRates(Integer sourceId, Date dateFrom) throws DataSourceException {
		List<CurrencyData> currencies = Lists.newArrayList();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		String selectSQL = " SELECT code, " + " ratio, " + " buy, " + " sell, " + "	date, " + "	source "
				+ "   FROM cbg_currencies" + " WHERE DATE(date) = ? " + " AND source = ? ORDER BY date asc";

		// SELECT * FROM `cbg_fixedcurrencies` WHERE year(column_curr_date) >=
		// 2016

		String json = null;
		// XXX test
		log.trace("Selected rows {} in {}", selectSQL, selectSQL);

		try {

			// Dynamic currencies
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setDate(1, DateTimeUtils.toSqlDate(dateFrom));
			preparedStatement.setInt(2, sourceId);
			rs = preparedStatement.executeQuery();

			while (rs.next()) {

				currencies.add(new CurrencyData(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4),
						rs.getTimestamp(5), rs.getInt(6)));

			}

			Gson gson = new GsonBuilder().setDateFormat(Defs.DATEFORMAT_ISO_8601).create();
			Type type = new TypeToken<List<CurrencyData>>() {
			}.getType();
			json = gson.toJson(currencies, type);

			System.out.println(json);
			List<CurrencyData> fromJson = gson.fromJson(json, type);

			// for (CurrencyData task : fromJson) {
			// System.out.println(task.getCode());
			// System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(),
			// "yyyy-MM-dd"));
			// }

		} catch (SQLException e) {
			throw new DataSourceException("SQL Exception in method getAllRatesByDate!", e);

		} finally {
			// TODO - close 2 PreprareStatement
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet(for selectSQL) in method getAllRatesByDate!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement(for selectSQL) in method getAllRatesByDate!", e);
				}
			}

		}

		return json;
	}

	@Override
	public String getAllRates(Date dateFrom) throws DataSourceException {
		List<CurrencyData> currencies = Lists.newArrayList();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		String selectSQL = " SELECT code, " + " ratio, " + " buy, " + " sell, " + "	date, " + "	source "
				+ "   FROM cbg_currencies" + " WHERE DATE(date) = ?  ORDER BY date asc";

		// SELECT * FROM `cbg_fixedcurrencies` WHERE year(column_curr_date) >=
		// 2016

		String json = null;
		// XXX test

		if (log.isTraceEnabled()) {
			log.trace("Selected rows {} in {}", selectSQL, selectSQL);
		}

		try {

			// Dynamic currencies
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setDate(1, DateTimeUtils.toSqlDate(dateFrom));
			rs = preparedStatement.executeQuery();

			while (rs.next()) {

				currencies.add(new CurrencyData(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4),
						rs.getTimestamp(5), rs.getInt(6)));

			}

			// Gson gson = new
			// GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			// Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd
			// hh:mm:ss").create();
			Gson gson = new GsonBuilder().setDateFormat(Defs.DATEFORMAT_ISO_8601).create();
			Type type = new TypeToken<List<CurrencyData>>() {
			}.getType();
			json = gson.toJson(currencies, type);

			List<CurrencyData> fromJson = gson.fromJson(json, type);

			// for (CurrencyData task : fromJson) {
			// System.out.println(task.getCode());
			// System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(),
			// "yyyy-MM-dd"));
			// }

		} catch (SQLException e) {
			throw new DataSourceException("SQL Exception in method getAllRatesByDate!", e);

		} finally {
			// TODO - close 2 PreprareStatement
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet(for selectSQL) in method getAllRatesByDate!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement(for selectSQL) in method getAllRatesByDate!", e);
				}
			}

		}

		return json;
	}

	@Override
	public CurrencySource getSourceById(int id) throws DataSourceException {
		CurrencySource source = new CurrencySource();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		String sqlSelect = "SELECT source_id, status, update_period, last_update, update_restrictions FROM cbg_sources "
				+ " WHERE source_id = ? and status = 0 ";

		try {
			preparedStatement = dbConnection.prepareStatement(sqlSelect);
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();

			if (rs.next()) {
				source.setSourceId(rs.getInt(1));
				source.setStatus(rs.getInt(2));
				source.setUpdatePeriod(rs.getInt(3));
				source.setLastUpdate(rs.getTimestamp(4));
				if (rs.getString(5) != null) {
					SourceUpdateRestrictions updateInfo = new Gson().fromJson(rs.getString(5),
							new TypeToken<SourceUpdateRestrictions>() {
							}.getType());
					source.setUpdateRestrictions(updateInfo);
				} else {
					source.setUpdateRestrictions(SourceUpdateRestrictions.empty());
				}
			}

		} catch (JsonParseException e) {
			throw new DataSourceException(id + " - could not parse update info JSON data for currency!", e);
		} catch (SQLException e) {
			throw new DataSourceException(id + " - SQL Exception in method getSourceById!", e);
		} finally {
			// TODO - close 2 PreprareStatement
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet(for selectSQL) in method getSourceById!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement(for selectSQL) in method getSourceById!", e);
				}
			}

		}
		return source;
	}

	@Override
	public List<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException {
		CurrencySource source = new CurrencySource();
		List<CurrencySource> listSource = Lists.newArrayList();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		String sqlSelect = "SELECT source_id, status, update_period, last_update, update_restrictions FROM cbg_sources ";
		if (isActiveOnly) {
			sqlSelect += "WHERE status = 0 ";
		}

		try {
			preparedStatement = dbConnection.prepareStatement(sqlSelect.toString());
			rs = preparedStatement.executeQuery();

			while (rs.next()) {
				source.setSourceId(rs.getInt(1));
				source.setStatus(rs.getInt(2));
				source.setUpdatePeriod(rs.getInt(3));
				source.setLastUpdate(rs.getTimestamp(4));

				try {
					if (rs.getString(5) != null) {
						SourceUpdateRestrictions updateInfo = new Gson().fromJson(rs.getString(5),
								new TypeToken<SourceUpdateRestrictions>() {
								}.getType());
						source.setUpdateRestrictions(updateInfo);
					} else {
						source.setUpdateRestrictions(SourceUpdateRestrictions.empty());
					}

					listSource.add(source);
				} catch (JsonParseException e) {
					throw new DataSourceException(
							source.getSourceId() + " - could not parse update info JSON data for currency!", e);
				}

				source = new CurrencySource();
			}

		} catch (SQLException e) {
			throw new DataSourceException("SQL Exception in method getAllSources!", e);
		} finally {
			// TODO - close 2 PreprareStatement
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet(for selectSQL) in method getAllSources!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement(for selectSQL) in method getAllSources!", e);
				}
			}

		}
		return listSource;
	}

}
