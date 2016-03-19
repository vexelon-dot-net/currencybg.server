package net.vexelon.currencybg.srv.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

	public static Date parseStringToDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	public static String parseDateToString(Date date, String dateFormat) {
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	public static java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static int getYearByDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return year;
	}
}
