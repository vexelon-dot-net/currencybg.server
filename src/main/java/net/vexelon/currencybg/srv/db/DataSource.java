package net.vexelon.currencybg.srv.db;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.vexelon.currencybg.srv.api.Currencies;
import net.vexelon.currencybg.srv.db.models.CurrencyData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataSource implements DataSourceInterface {

	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_NAME = "currencybg";
	private static final String DB_HOST = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
	private static final String DB_PORT = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
	private static final String DB_CONNECTION = String.format("jdbc:mysql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
	private static final String DB_USER = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
	private static final String DB_PASSWORD = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");

	private static final Logger log = LoggerFactory.getLogger(Currencies.class);

	Connection dbConnection = null;

	@Override
	public Connection dbConnect() throws DataSourceException {

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {
			log.error("Could not open database connection!", e);
			throw new DataSourceException("Could not open SQLite database!", e);
			// System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {
			log.error("Could not open database connection!", e);
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

	// @Override
	// public String selectRecordsFromTable() throws DataSourceException {
	// // Connection dbConnection = null;
	// PreparedStatement preparedStatement = null;
	//
	// String selectSQL = "SELECT ID FROM temp";
	// StringBuffer result = new StringBuffer();
	//
	// // XXX test
	// log.trace("Selected rows {} in {}", selectSQL, selectSQL);
	//
	// try {
	// // dbConnection = getDBConnection();
	// preparedStatement = dbConnection.prepareStatement(selectSQL);
	// // preparedStatement.setInt(1, 1001);
	//
	// // execute select SQL stetement
	// ResultSet rs = preparedStatement.executeQuery();
	//
	// while (rs.next()) {
	//
	// // String userid = rs.getString("USER_ID");
	// String id = rs.getString("ID");
	//
	// result.append(id);
	// // System.out.println("userid : " + userid);
	// // System.out.println("ID : " + id);
	//
	// }
	//
	// } catch (SQLException e) {
	// log.error("Error selecting rows!", e); // XXX test
	//
	// System.out.println(e.getMessage());
	//
	// } finally {
	//
	// if (preparedStatement != null) {
	// try {
	// preparedStatement.close();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// if (dbConnection != null) {
	// try {
	// dbConnection.close();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }
	// return result.toString();
	// }

	@Override
	public String selectRecordsFromTable() throws DataSourceException {
		// Connection dbConnection = null;
		List<CurrencyData> currencies = new ArrayList<CurrencyData>();
		CurrencyData currency = new CurrencyData();

		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT column_gold, column_name, column_code, column_ratio, column_reverserate, column_rate, column_curr_date, column_locale FROM cbg_currencies";
		StringBuffer result = new StringBuffer();
		String json = null;
		// XXX test
		log.trace("Selected rows {} in {}", selectSQL, selectSQL);

		try {
			// dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			// preparedStatement.setInt(1, 1001);

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				currencies.add(new CurrencyData(rs.getString(2), rs.getString(3)));

				// currency.setGold(rs.getInt(1));
				// currency.setName(rs.getString(2));
				// currency.setCode(rs.getString(3));
				// currency.setRatio(rs.getInt(4));
				// currency.setReverseRate(rs.getString(5));
				// currency.setRate(rs.getString(6));
				// currency.setCurrDate(rs.getDate(7));
				// currency.setLocation(rs.getString(8));
				//
				// currencies.add(currency);
				//
				// currency = new CurrencyData();

			}

			Gson gson = new Gson();
			Type type = new TypeToken<List<CurrencyData>>() {}.getType();
			json = gson.toJson(currencies, type);

			System.out.println(json);
			List<CurrencyData> fromJson = gson.fromJson(json, type);

			for (CurrencyData task : fromJson) {
				// System.out.println(task);
				System.out.println(task.getCode());
			}

		} catch (SQLException e) {
			log.error("Error selecting rows!", e); // XXX test

			System.out.println(e.getMessage());

		}
		return json;
		// return result.toString();
	}

	@Override
	public/* List<CurrencyData> */String getRates(/* CurrencyLocales */String locale) throws DataSourceException {

		List<CurrencyData> list = new ArrayList<CurrencyData>();
		for (int i = 0; i < 10; i++) {
			list.add(new CurrencyData("Test", i + ""));
		}
		Gson gson = new Gson();
		Type type = new TypeToken<List<CurrencyData>>() {}.getType();
		String json = gson.toJson(list, type);

		// System.out.println(json);
		// List<CurrencyData> fromJson = gson.fromJson(json, type);
		//
		// for (CurrencyData task : fromJson) {
		// System.out.println(task);
		// System.out.println(task.getCode());
		// }

		return json;

	}

	/*
	 * @Override
	 * public void connect(Context context) throws DataSourceException {
	 * try {
	 * dbHelper = new CurrenciesSQLiteDB(context);
	 * database = dbHelper.getWritableDatabase();
	 * } catch (SQLException e) {
	 * throw new DataSourceException("Could not open SQLite database!", e);
	 * }
	 * }
	 */
	// @Override
	// public void close() {
	// if (dbHelper != null) {
	// dbHelper.close();
	// }
	// }

	private Date parseStringToDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	private String parseDateToString(Date date, String dateFormat) {
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	// @Override
	// public void addRates(Map<CurrencyLocales, List<Currencies>> rates) throws
	// DataSourceException {
	// ContentValues values = new ContentValues();
	// ContentValues valuesDate = new ContentValues();
	//
	// for (Map.Entry<CurrencyLocales, List<Currencies>> currenciesData :
	// rates.entrySet()) {
	//
	// // Данните от сайта на БНБ се разделят на
	// два списъка - от динамични
	// // валути и от статични валути
	// List<Currencies> dynamicCurrencies = new ArrayList<Currencies>();
	// List<Currencies> fixedCurrencies = new ArrayList<Currencies>();
	// for (Currencies currency : currenciesData.getValue()) {
	// if (currency.isFixed()) {
	// fixedCurrencies.add(currency);
	// } else {
	// dynamicCurrencies.add(currency);
	// }
	// }
	//
	// // //За всеки от списъците се прави
	// проверка дали го има в базата.
	// // За динамични валути
	// // TODO - да се ползва новия метод
	// if (!isHaveRates(currenciesData.getKey(),
	// dynamicCurrencies.get(1).getCurrDate(), false)) {
	// for (int i = 0; i < dynamicCurrencies.size(); i++) {
	// values.put(Defs.COLUMN_GOLD, dynamicCurrencies.get(i).getGold());
	// values.put(Defs.COLUMN_NAME, dynamicCurrencies.get(i).getName());
	// values.put(Defs.COLUMN_CODE, dynamicCurrencies.get(i).getCode());
	// values.put(Defs.COLUMN_RATIO, dynamicCurrencies.get(i).getRatio());
	// values.put(Defs.COLUMN_REVERSERATE,
	// dynamicCurrencies.get(i).getReverseRate());
	// values.put(Defs.COLUMN_RATE, dynamicCurrencies.get(i).getRate());
	// values.put(Defs.COLUMN_EXTRAINFO,
	// dynamicCurrencies.get(i).getExtraInfo());
	// values.put(Defs.COLUMN_CURR_DATE,
	// parseDateToString(dynamicCurrencies.get(i).getCurrDate(), "yyyy-MM-dd"));
	// values.put(Defs.COLUMN_TITLE, dynamicCurrencies.get(i).getTitle());
	// values.put(Defs.COLUMN_F_STAR, dynamicCurrencies.get(i).getfStar());
	// values.put(Defs.COLUMN_LOCALE, currenciesData.getKey().toString());
	//
	// database.insert(Defs.TABLE_CURRENCY, null, values);// TODO
	// // remove
	// // comment
	// values = new ContentValues();
	//
	// }
	//
	// valuesDate.put(Defs.COLUMN_CURR_DATE,
	// parseDateToString(currenciesData.getValue().get(1).getCurrDate(),
	// "yyyy-MM-dd"));
	// valuesDate.put(Defs.COLUMN_LOCALE, currenciesData.getKey().toString());
	// database.insert(Defs.TABLE_CURRENCY_DATE, null, valuesDate);// TODO
	// // remove
	// // comment
	//
	// valuesDate = new ContentValues();
	// }
	//
	// // За фиксирани валути. Може да го има
	// вече в базата, защото се
	// // добавят веднъж годишно
	// if (fixedCurrencies.size() > 0) {
	// if (!isHaveRates(currenciesData.getKey(),
	// fixedCurrencies.get(1).getCurrDate(), true)) {
	// for (int i = 0; i < fixedCurrencies.size(); i++) {
	// values.put(Defs.COLUMN_GOLD, fixedCurrencies.get(i).getGold());
	// values.put(Defs.COLUMN_NAME, fixedCurrencies.get(i).getName());
	// values.put(Defs.COLUMN_CODE, fixedCurrencies.get(i).getCode());
	// values.put(Defs.COLUMN_RATIO, fixedCurrencies.get(i).getRatio());
	// values.put(Defs.COLUMN_REVERSERATE,
	// fixedCurrencies.get(i).getReverseRate());
	// values.put(Defs.COLUMN_RATE, fixedCurrencies.get(i).getRate());
	// values.put(Defs.COLUMN_EXTRAINFO, fixedCurrencies.get(i).getExtraInfo());
	// values.put(Defs.COLUMN_CURR_DATE,
	// parseDateToString(fixedCurrencies.get(i).getCurrDate(), "yyyy-MM-dd"));
	// values.put(Defs.COLUMN_TITLE, fixedCurrencies.get(i).getTitle());
	// values.put(Defs.COLUMN_F_STAR, fixedCurrencies.get(i).getfStar());
	// values.put(Defs.COLUMN_LOCALE, currenciesData.getKey().toString());
	//
	// database.insert(Defs.TABLE_FIXED_CURRENCY, null, values);// TODO
	// // remove
	// // comment
	// values = new ContentValues();
	// }
	// }
	// }
	// }
	// }

	// private Boolean isHaveRates(CurrencyLocales locale, Date dateOfCurrency,
	// boolean isFixed) {
	// String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
	// String whereClause = Defs.COLUMN_CURR_DATE + " = ? AND " +
	// Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs = new String[] { parseDateToString(dateOfCurrency,
	// "yyyy-MM-dd"), locale.toString() };
	//
	// Cursor cursor = null;
	// if (isFixed) {
	// cursor = database.query(Defs.TABLE_FIXED_CURRENCY, tableColumns,
	// whereClause, whereArgs, null, null, null);
	// } else {
	// cursor = database.query(Defs.TABLE_CURRENCY_DATE, tableColumns,
	// whereClause, whereArgs, null, null, null);
	// }
	// if (cursor.moveToFirst()) {
	// cursor.close();
	// return true;
	// } else {
	// cursor.close();
	// return false;
	// }
	// }

	// @Override
	// public List<Currencies> getLastRates(CurrencyLocales locale) throws
	// DataSourceException {
	// List<Currencies> lastRates = Lists.newArrayList();
	// String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
	// String whereClause = Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs = new String[] { locale.toString() };
	//
	// Cursor cursor = database.query(Defs.TABLE_CURRENCY_DATE, tableColumns,
	// whereClause, whereArgs, null, null,
	// Defs.COLUMN_CURR_DATE + " DESC");
	//
	// if (cursor.moveToFirst()) {
	// String whereClause2 = Defs.COLUMN_CURR_DATE + " = ? AND " +
	// Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs2 = new String[] {
	// cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CURR_DATE)),
	// locale.toString() };
	//
	// Cursor cursor2 = database.query(Defs.TABLE_CURRENCY, ALL_COLUMNS,
	// whereClause2, whereArgs2, null, null,
	// null);
	//
	// cursor2.moveToFirst();
	// while (!cursor2.isAfterLast()) {
	// Currencies comment = cursorToCurrency(cursor2, false);
	// lastRates.add(comment);
	// cursor2.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor2.close();
	//
	// }
	// cursor.close();
	//
	// return lastRates;
	// }

	// @Override
	// public List<Currencies> getLastFixedRates(/*CurrencyLocales*/String
	// locale) throws DataSourceException {
	// List<Currencies> lastRates = Lists.newArrayList();
	// try {
	// String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
	// String whereClause = Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs = new String[] { locale.toString() };
	//
	// // Cursor cursor = database.query(Defs.TABLE_FIXED_CURRENCY,
	// // tableColumns, whereClause, whereArgs, null,
	// // null,Defs.COLUMN_CURR_DATE + " DESC");
	// Cursor cursor = database.query(true, Defs.TABLE_FIXED_CURRENCY,
	// tableColumns, whereClause, whereArgs, null,
	// null, Defs.COLUMN_CURR_DATE + " DESC", null);
	//
	// if (cursor.moveToFirst()) {
	// String whereClause2 = Defs.COLUMN_CURR_DATE + " = ? AND " +
	// Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs2 = new String[] {
	// cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CURR_DATE)),
	// locale.toString() };
	//
	// Cursor cursor2 = database.query(Defs.TABLE_FIXED_CURRENCY, ALL_COLUMNS,
	// whereClause2, whereArgs2, null,
	// null, null);
	//
	// cursor2.moveToFirst();
	// while (!cursor2.isAfterLast()) {
	// Currencies comment = cursorToCurrency(cursor2, true);
	// lastRates.add(comment);
	// cursor2.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor2.close();
	// }
	// cursor.close();
	//
	// } catch (SQLiteException s) {
	// // TODO: move to onUpgrade
	// database.execSQL(dbHelper.CREATE_TABLE_FIXED_CURRENCY);
	// }
	// return lastRates;
	// }

	// @Override
	// public List<Date> getAvailableRatesDates(/*CurrencyLocales*/String
	// locale) throws DataSourceException {
	// List<Date> resultCurrency = Lists.newArrayList();
	// String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
	// String whereClause = Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs = new String[] { locale.toString() };
	//
	// Cursor cursor = database.query(true, Defs.TABLE_CURRENCY_DATE,
	// tableColumns, whereClause, whereArgs, null,
	// null, null, null);
	//
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast()) {
	// try {
	// resultCurrency.add(parseStringToDate(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CURR_DATE)),
	// "yyyy-MM-dd"));
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// throw new DataSourceException(e);
	// }
	//
	// cursor.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor.close();
	// return resultCurrency;
	// }

	// @Override
	// public List<Currencies> getFixedRates(CurrencyLocales locale, Date
	// dateOfCurrency) throws DataSourceException {
	// List<Currencies> resultFixedCurrency = null;
	// Cursor cursor = null;
	// try {
	// resultFixedCurrency = new ArrayList<Currencies>();
	// String whereClause = Defs.COLUMN_CURR_DATE + " = ? AND " +
	// Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs = new String[] { parseDateToString(dateOfCurrency,
	// "yyyy-MM-dd"), locale.toString() };
	//
	// cursor = database.query(Defs.TABLE_FIXED_CURRENCY, ALL_COLUMNS,
	// whereClause, whereArgs, null, null, null);
	//
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast()) {
	// Currencies comment = cursorToCurrency(cursor, false);
	// resultFixedCurrency.add(comment);
	// cursor.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor.close();
	//
	// } catch (SQLiteException s) {
	// database.execSQL(dbHelper.CREATE_TABLE_FIXED_CURRENCY);
	// } finally {
	// if (cursor != null) {
	// cursor.close();
	// }
	//
	// }
	//
	// return resultFixedCurrency;
	// }

	// @Override
	// public List<Currencies> getRates(CurrencyLocales locale) throws
	// DataSourceException {
	// List<Currencies> currencies = new ArrayList<Currencies>();
	// String whereClause = Defs.COLUMN_LOCALE + " = ? ";
	// String[] whereArgs = new String[] { locale.toString() };
	//
	// Cursor cursor = database.query(Defs.TABLE_CURRENCY, ALL_COLUMNS,
	// whereClause, whereArgs, null, null, null);
	//
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast()) {
	// Currencies comment = cursorToCurrency(cursor, false);
	// currencies.add(comment);
	// cursor.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor.close();
	// return currencies;
	// }

}
