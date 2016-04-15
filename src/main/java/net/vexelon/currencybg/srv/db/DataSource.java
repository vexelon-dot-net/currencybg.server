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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.Currencies;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencyLocales;
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
	public void addRates(Map<CurrencyLocales, List<CurrencyData>> rates) throws DataSourceException {

		for (Map.Entry<CurrencyLocales, List<CurrencyData>> currenciesData : rates.entrySet()) {

			// Данните от сайта на БНБ се разделят на два списъка - от динамични
			// валути и от статични валути
			List<CurrencyData> dynamicCurrencies = new ArrayList<CurrencyData>();
			List<CurrencyData> fixedCurrencies = new ArrayList<CurrencyData>();
			for (CurrencyData currency : currenciesData.getValue()) {
				if (currency.isFixed()) {
					fixedCurrencies.add(currency);
				} else {
					dynamicCurrencies.add(currency);
				}
			}

			// make sure there is sufficient data in the lists
			if (dynamicCurrencies.isEmpty() || dynamicCurrencies.size() < 2) {
				log.debug("Dynamic currencies list is empty (" + dynamicCurrencies.size() + ")!");
				return;
			}
			if (fixedCurrencies.isEmpty() || fixedCurrencies.size() < 2) {
				log.debug("Fixed currencies list is empty (" + fixedCurrencies.size() + ")!");
				return;
			}

			// //За всеки от списъците се прави проверка дали го има в базата.
			// За динамични валути
			// TODO - да се ползва новия метод

			if (!isHaveRates(currenciesData.getKey(), dynamicCurrencies.get(1).getCurrDate(), false)) {

				PreparedStatement preparedStatement = null;
				PreparedStatement preparedStatementDate = null;
				String insertSQL = "INSERT INTO cbg_currencies(COLUMN_GOLD, COLUMN_NAME, COLUMN_CODE, COLUMN_RATIO, COLUMN_REVERSERATE, COLUMN_RATE, COLUMN_EXTRAINFO, COLUMN_CURR_DATE, COLUMN_TITLE, COLUMN_F_STAR, COLUMN_LOCALE) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
				String insertSQLDate = "INSERT INTO cbg_currenciesdate(COLUMN_CURR_DATE, COLUMN_LOCALE) VALUES (?,?)";

				try {
					preparedStatement = dbConnection.prepareStatement(insertSQL);

					for (int i = 0; i < dynamicCurrencies.size(); i++) {

						preparedStatement.setInt(1, dynamicCurrencies.get(i).getGold());
						preparedStatement.setString(2, dynamicCurrencies.get(i).getName());
						preparedStatement.setString(3, dynamicCurrencies.get(i).getCode());
						preparedStatement.setInt(4, dynamicCurrencies.get(i).getRatio());
						preparedStatement.setFloat(5, Float.parseFloat(dynamicCurrencies.get(i).getReverseRate()));
						preparedStatement.setFloat(6, Float.parseFloat(dynamicCurrencies.get(i).getRate()));
						preparedStatement.setString(7, dynamicCurrencies.get(i).getExtraInfo());
						preparedStatement.setDate(8,
								new java.sql.Date(dynamicCurrencies.get(i).getCurrDate().getTime()));
						preparedStatement.setString(9, dynamicCurrencies.get(i).getTitle());
						preparedStatement.setInt(10, dynamicCurrencies.get(i).getfStar());
						preparedStatement.setString(11, currenciesData.getKey().toString());

						preparedStatement.executeUpdate();

					}

					preparedStatementDate = dbConnection.prepareStatement(insertSQLDate);

					preparedStatementDate.setDate(1,
							new java.sql.Date(currenciesData.getValue().get(1).getCurrDate().getTime()));
					preparedStatementDate.setString(2, currenciesData.getKey().toString());

					preparedStatementDate.executeUpdate();

				} catch (SQLException e) {
					log.error("SQL Exception in method isHaveRates!", e);
					throw new DataSourceException("SQL Exception in method addRates!", e);

				} finally {

					if (preparedStatement != null) {
						try {
							preparedStatement.close();
						} catch (SQLException e) {
							log.error("Problem with close of PreparedStatement in method addRates!", e);
						}
					}

					if (preparedStatementDate != null) {
						try {
							preparedStatementDate.close();
						} catch (SQLException e) {
							log.error("Problem with close of PreparedStatement in method addRates!", e);
						}
					}
				}

			}

			// За фиксирани валути. Може да го има вече в базата, защото се
			// добавят веднъж годишно
			if (fixedCurrencies.size() > 0) {
				if (!isHaveRates(currenciesData.getKey(), fixedCurrencies.get(1).getCurrDate(), true)) {

					PreparedStatement preparedStatement = null;
					String insertSQL = "INSERT INTO cbg_fixedcurrencies(COLUMN_GOLD, COLUMN_NAME, COLUMN_CODE, COLUMN_RATIO, COLUMN_REVERSERATE, COLUMN_RATE, COLUMN_EXTRAINFO, COLUMN_CURR_DATE, COLUMN_TITLE, COLUMN_F_STAR, COLUMN_LOCALE) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

					try {
						preparedStatement = dbConnection.prepareStatement(insertSQL);

						for (int i = 0; i < fixedCurrencies.size(); i++) {

							preparedStatement.setInt(1, fixedCurrencies.get(i).getGold());
							preparedStatement.setString(2, fixedCurrencies.get(i).getName());
							preparedStatement.setString(3, fixedCurrencies.get(i).getCode());
							preparedStatement.setInt(4, fixedCurrencies.get(i).getRatio());
							preparedStatement.setFloat(5, Float.parseFloat(fixedCurrencies.get(i).getReverseRate()));
							preparedStatement.setFloat(6, Float.parseFloat(fixedCurrencies.get(i).getRate()));
							preparedStatement.setString(7, fixedCurrencies.get(i).getExtraInfo());
							preparedStatement.setDate(8,
									new java.sql.Date(fixedCurrencies.get(i).getCurrDate().getTime()));
							preparedStatement.setString(9, fixedCurrencies.get(i).getTitle());
							preparedStatement.setInt(10, fixedCurrencies.get(i).getfStar());
							preparedStatement.setString(11, currenciesData.getKey().toString());

							preparedStatement.executeUpdate();

						}

					} catch (SQLException e) {
						log.error("SQL Exception in method isHaveRates!", e);
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
		}
	}

	private Boolean isHaveRates(CurrencyLocales locale, Date dateOfCurrency, boolean isFixed)
			throws DataSourceException {
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		StringBuilder selectSQL = new StringBuilder("SELECT * FROM");
		if (isFixed) {
			selectSQL.append("cbg_fixedcurrencies");
		} else {
			selectSQL.append("cbg_currenciesdate");
		}
		selectSQL.append("WHERE column_curr_date = ? and column_local = ?");

		try {
			preparedStatement = dbConnection.prepareStatement(selectSQL.toString());
			preparedStatement.setDate(1, new java.sql.Date(dateOfCurrency.getTime()));
			preparedStatement.setString(2, locale.toString());
			rs = preparedStatement.executeQuery();

			if (rs.next())
				return true;

		} catch (SQLException e) {
			log.error("SQL Exception in method isHaveRates!", e);
			throw new DataSourceException("SQL Exception in method getFixedRates!", e);

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet in method isHaveRates!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement in method isHaveRates!", e);
				}
			}

		}

		return false;
	}

	@Override
	public boolean checkAuthentication(String authenticationKey) throws DataSourceException {

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		String selectSQL = "SELECT 1 FROM cbg_apikeys WHERE key_value = ?";
		try {
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setString(1, authenticationKey);
			rs = preparedStatement.executeQuery();

			if (rs.next())
				return true;
		} catch (SQLException e) {
			log.error("SQL Exception in method getFixedRates!", e);
			throw new DataSourceException("SQL Exception in method getFixedRates!", e);

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Problem with close of ResultSet in method checkAuthentication!", e);
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("Problem with close of PreparedStatement in method checkAuthentication!", e);
				}
			}

		}

		return false;
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

			// for (CurrencyData task : fromJson) {
			// System.out.println(task.getCode());
			// System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(),
			// "yyyy-MM-dd"));
			// }

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

			// for (CurrencyData task : fromJson) {
			// System.out.println(task.getCode());
			// System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(),
			// "yyyy-MM-dd"));
			// }

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

			// for (CurrencyData task : fromJson) {
			// System.out.println(task.getCode());
			// System.out.println(DateTimeUtils.parseDateToString(task.getCurrDate(),
			// "yyyy-MM-dd"));
			// }

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
