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

	/**
	 * Add define days to some date
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {

		SimpleDateFormat sdf = new SimpleDateFormat(Defs.DATE_FORMAT);
		try {
			date = sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	/**
	 * Modify Date format. Example: "yyyy-MM-dd'T'HH:mm:ssZ => yyyy-MM-dd
	 * HH:mm:ss
	 * 
	 * @param inputDate
	 * @param inputFormatTimeFormat
	 * @param outputFormatTimeFormat
	 * @return
	 * @throws ParseException
	 */
	public static String modifyDateLayout(String inputDate, String inputFormatTimeFormat, String outputFormatTimeFormat)
			throws ParseException {

		Date date = new SimpleDateFormat(inputFormatTimeFormat).parse(inputDate);
		return new SimpleDateFormat(outputFormatTimeFormat).format(date);
	}

	/**
	 * Convert Date from one timezone to another timezone
	 * 
	 * @param phoneTimeZone
	 * @param currentTimeZone
	 * @param formatTimeZone
	 * @return
	 * @throws ParseException
	 */
	public static String convertPhoneToCurrentTimeZone(String phoneTimeZone, String currentTimeZone,
			String formatTimeZone) throws ParseException {

		DateFormat dateFormat = new SimpleDateFormat(formatTimeZone);

		TimeZone tzInBulgaria = TimeZone.getTimeZone(currentTimeZone);

		dateFormat.setTimeZone(tzInBulgaria);
		String sDateInBulgaria = dateFormat.format(parseStringToDate(phoneTimeZone, formatTimeZone));

		return sDateInBulgaria;

	}

	public static int getYearByDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static Date getStartOfYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String dateInString = "01.01." + year;
		Date currentYear = null;
		try {
			currentYear = sdf.parse(dateInString);
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
