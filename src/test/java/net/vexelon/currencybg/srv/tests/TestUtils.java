package net.vexelon.currencybg.srv.tests;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class TestUtils {

	public final static String TIME_FORMAT = "HH:mm";

	public final static String TIMEZONE_BERLIN = "Europe/Berlin";
	public final static String TIMEZONE_SOFIA = "Europe/Sofia";
	public final static String TIMEZONE_NEWYORK = "America/New_York";

	public static InputStream getTestResource(String path) {
		return TestUtils.class.getResourceAsStream(path);
	}

	public static Date newDate(int y, int m, int d) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d);
		return cal.getTime();
	}

	public static Date newDate(int y, int m, int d, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d);
		return cal.getTime();
	}

	public static Calendar newCal(int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(now);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static Calendar newCal(int hour, int minute, String timeZone) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static Date newDate(int hour, int minute) {
		return newCal(hour, minute).getTime();
	}

}
