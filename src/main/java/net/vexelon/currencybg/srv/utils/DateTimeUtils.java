package net.vexelon.currencybg.srv.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.vexelon.currencybg.srv.Defs;

public class DateTimeUtils {

	protected TimeZone timeZone;

	/**
	 * 
	 * @param timeZone
	 */
	public DateTimeUtils(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public DateTimeUtils(String timeZone) {
		this(TimeZone.getTimeZone(timeZone));
	}

	public static Date parseDateISO8601(String date) throws ParseException {
		Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(date);
		return cal.getTime();
	}

	public static String toStringISO8601(Date date, String timeZone) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setTimeZone(TimeZone.getTimeZone(timeZone));
		return javax.xml.bind.DatatypeConverter.printDateTime(cal);
	}

	public static Date parseDate(String date, String pattern) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(date);
	}

	public static String toString(Date date, String pattern) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	public static java.sql.Date toSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	/**
	 * Add define days to some date
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	/**
	 * Removes the time zone portion of {@code date}. The time zone must be
	 * explicitly specified with {@code timeZone}.
	 * <p>
	 * Example: <i>yyyy-MM-dd'T'HH:mm:ssZ</i> to <i>yyyy-MM-dd HH:mm:ss</i>.
	 * 
	 * @param date
	 * @param timeZone
	 * @return
	 * @throws ParseException
	 *             On {@code date} parse error.
	 */
	public static String removeTimeZone(String date, String timeZone) throws ParseException {
		DateFormat dateFormatOutput = new SimpleDateFormat(Defs.DATETIME_FORMAT);
		dateFormatOutput.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFormatOutput.format(parseDateISO8601(date));
	}

	/**
	 * Convert ISO8601 {@code date} to date in given {@code toTimeZone}.
	 * 
	 * @param date
	 * @param toTimeZone
	 * @return
	 * @throws ParseException
	 *             On {@code date} parse error.
	 */
	public static String toTimeZone(String date, String toTimeZone) throws ParseException {
		// DateFormat dateFormat = new SimpleDateFormat(datePattern);
		// dateFormat.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
		// return dateFormat.format(parseDateISO8601(date));
		return toStringISO8601(parseDateISO8601(date), toTimeZone);
	}

	public static int getYearByDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static Date getStartOfYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String dateInString = "01.01." + year;
		Date currentYear = null;
		try {
			currentYear = dateFormat.parse(dateInString);
		} catch (ParseException e1) {
			e1.printStackTrace();
			// use default (today)
		}
		return currentYear;
	}

	public Calendar toCalendar(String value, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = dateFormat.parse(value);
		Calendar calendar = getCal(date);

		Calendar today = getCalToday();
		calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, today.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, today.get(Calendar.DATE));

		return calendar;
	}

	public Calendar getCal(Date date) {
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(date);
		return cal;
	}

	public Calendar getCalToday() {
		return getCal(new Date());
	}

	public boolean isSaturday(Date date) {
		return getCal(date).get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}

	public boolean isSunday(Date date) {
		return getCal(date).get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public boolean isWeekend(Date date) {
		return isSaturday(date) || isSunday(date);
	}

	public boolean isWeekday(Date date) {
		return !isWeekend(date);
	}
}
