package net.vexelon.currencybg.srv.tests;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public final class TestUtils {

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
		System.out.println(cal.getTime());
		return cal.getTime();
	}
}
