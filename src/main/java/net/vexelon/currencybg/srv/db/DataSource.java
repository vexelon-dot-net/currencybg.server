package net.vexelon.currencybg.srv.db;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.Currencies;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class DataSource implements DataSourceInterface {

	private static final Logger log = LoggerFactory.getLogger(Currencies.class);

	Connection dbConnection = null;

	@Override
	public Connection dbConnect() throws DataSourceException {

		try {

			Class.forName(Defs.DB_DRIVER);

		} catch (ClassNotFoundException e) {
			// log.error("Could not open database connection!", e);
			throw new DataSourceException("Could not find DB driver!", e);
			// System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(Defs.DB_CONNECTION, Defs.DB_USER, Defs.DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {
			// log.error("Could not open database connection!", e);
			throw new DataSourceException("Could not open SQLite database!", e);
			// System.out.println(e.getMessage());

		}

		// return dbConnection;
	};

	@Override
	public void close() {
		if (dbConnection != null) {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				log.error("Could not close database connection!", e);
				// throw new
				// DataSourceException("Could not open SQLite database!", e);
			}
		}
	}

	@Override
	public String getAllRatesByDate(Date dateFrom) throws DataSourceException {
		List<CurrencyData> currencies = new ArrayList<CurrencyData>();

		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementFixed = null;
		ResultSet rs = null;
		ResultSet rsFixed = null;

		String selectSQL = " SELECT column_gold, " + "		column_name, " + "		column_code, "
				+ "		column_ratio, " + "		column_reverserate, " + "		column_rate, "
				+ "		column_extrainfo, " + "		column_curr_date, " + "		column_title, " + "		column_f_star, "
				+ "		column_locale," + " false " + "   FROM cbg_currencies" + " WHERE column_curr_date > ? ";

		String selectSQLFixed = " SELECT column_gold, " + "		column_name, " + "		column_code, "
				+ "		column_ratio, " + "		column_reverserate, " + "		column_rate, "
				+ "		column_extrainfo, " + "		column_curr_date, " + "		column_title, " + "		column_f_star, "
				+ "		column_locale," + " true " + "   FROM cbg_fixedcurrencies" + " WHERE column_curr_date > ? ";

		// SELECT * FROM `cbg_fixedcurrencies` WHERE year(column_curr_date) >=
		// 2016

		String json = null;
		// XXX test
		log.trace("Selected rows {} in {}", selectSQL, selectSQL);

		try {

			// Dynamic currencies
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setDate(1, DateTimeUtils.convertJavaDateToSqlDate(dateFrom));
			rs = preparedStatement.executeQuery();

			while (rs.next()) {

				currencies.add(new CurrencyData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getDate(8), rs.getString(9),

						rs.getInt(10), rs.getString(11), rs.getBoolean(12)));

			}

			// Fixed currencies
			preparedStatementFixed = dbConnection.prepareStatement(selectSQLFixed);
			preparedStatementFixed.setInt(1, DateTimeUtils.getYearByDate(dateFrom));
			rsFixed = preparedStatementFixed.executeQuery();

			while (rsFixed.next()) {

				currencies.add(new CurrencyData(rsFixed.getInt(1), rsFixed.getString(2), rsFixed.getString(3),
						rsFixed.getInt(4), rsFixed.getString(5), rsFixed.getString(6), rsFixed.getString(7),
						rsFixed.getDate(8), rsFixed.getString(9), rsFixed.getInt(10), rsFixed.getString(11),
						rsFixed.getBoolean(12)));

			}

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			Type type = new TypeToken<List<CurrencyData>>() {}.getType();
			json = gson.toJson(currencies, type);

			System.out.println(json);
			List<CurrencyData> fromJson = gson.fromJson(json, type);

			for (CurrencyData task : fromJson) {
				System.out.println(task.getCode());
				System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(), "yyyy-MM-dd"));
			}

		} catch (SQLException e) {
			log.error("SQL Exception in method getAllRatesByDate!", e);
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

			if (rsFixed != null) {
				try {
					rsFixed.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet(for selectSQLFixed) in method getAllRatesByDate!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement(for selectSQL) in method getAllRatesByDate!", e);
				}
			}

			if (preparedStatementFixed != null) {
				try {
					preparedStatementFixed.close();
				} catch (SQLException e) {
					log.error(
							"Problem with close of PreparedStatement(for selectSQLFixed) in method getAllRatesByDate!",
							e);
				}
			}

		}

		return json;
	}

	@Override
	public String getFixedRates(Date dateFrom) throws DataSourceException {
		List<CurrencyData> currencies = new ArrayList<CurrencyData>();

		PreparedStatement preparedStatementFixed = null;
		ResultSet rsFixed = null;

		String selectSQLFixed = " SELECT column_gold, " + "		column_name, " + "		column_code, "
				+ "		column_ratio, " + "		column_reverserate, " + "		column_rate, "
				+ "		column_extrainfo, " + "		column_curr_date, " + "		column_title, " + "		column_f_star, "
				+ "		column_locale," + " true " + "   FROM cbg_fixedcurrencies" + " WHERE column_curr_date > ? ";

		String json = null;

		try {

			preparedStatementFixed = dbConnection.prepareStatement(selectSQLFixed);
			preparedStatementFixed.setInt(1, DateTimeUtils.getYearByDate(dateFrom));
			rsFixed = preparedStatementFixed.executeQuery();

			while (rsFixed.next()) {

				currencies.add(new CurrencyData(rsFixed.getInt(1), rsFixed.getString(2), rsFixed.getString(3),
						rsFixed.getInt(4), rsFixed.getString(5), rsFixed.getString(6), rsFixed.getString(7),
						rsFixed.getDate(8), rsFixed.getString(9), rsFixed.getInt(10), rsFixed.getString(11),
						rsFixed.getBoolean(12)));

			}

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			Type type = new TypeToken<List<CurrencyData>>() {}.getType();
			json = gson.toJson(currencies, type);

			System.out.println(json);
			List<CurrencyData> fromJson = gson.fromJson(json, type);

			for (CurrencyData task : fromJson) {
				System.out.println(task.getCode());
				System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(), "yyyy-MM-dd"));
			}

		} catch (SQLException e) {
			log.error("SQL Exception in method getFixedRates!", e);
			throw new DataSourceException("SQL Exception in method getFixedRates!", e);

		} finally {

			if (rsFixed != null) {
				try {
					rsFixed.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet in method getFixedRates!", e);
				}
			}

			if (preparedStatementFixed != null) {
				try {
					preparedStatementFixed.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement in method getFixedRates!", e);
				}
			}

		}

		return json;
	}

	@Override
	public String getNonFixedRates(Date dateFrom) throws DataSourceException {
		List<CurrencyData> currencies = new ArrayList<CurrencyData>();

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		String selectSQL = " SELECT column_gold, " + "		column_name, " + "		column_code, "
				+ "		column_ratio, " + "		column_reverserate, " + "		column_rate, "
				+ "		column_extrainfo, " + "		column_curr_date, " + "		column_title, " + "		column_f_star, "
				+ "		column_locale," + " false " + "   FROM cbg_currencies" + " WHERE column_curr_date > ? ";
		String json = null;
		// XXX test
		log.trace("Selected rows {} in {}", selectSQL, selectSQL);

		try {
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setDate(1, DateTimeUtils.convertJavaDateToSqlDate(dateFrom));
			rs = preparedStatement.executeQuery();

			while (rs.next()) {

				currencies.add(new CurrencyData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getDate(8), rs.getString(9),
						rs.getInt(10), rs.getString(11), rs.getBoolean(12)));

			}

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			Type type = new TypeToken<List<CurrencyData>>() {}.getType();
			json = gson.toJson(currencies, type);

			System.out.println(json);
			List<CurrencyData> fromJson = gson.fromJson(json, type);

			for (CurrencyData task : fromJson) {
				System.out.println(task.getCode());
				System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(), "yyyy-MM-dd"));
			}

		} catch (SQLException e) {
			// TODO - Wrap of SQLException
			log.error("SQL Exception in method getNonfixedRates!", e);

			throw new DataSourceException("SQL Exception in method getNonfixedRates!", e);

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet in method getNonfixedRates!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement in method getNonfixedRates!", e);
				}
			}

		}

		return json;
	}

}
