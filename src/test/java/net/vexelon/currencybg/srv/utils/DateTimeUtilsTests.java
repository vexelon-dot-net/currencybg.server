package net.vexelon.currencybg.srv.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DateTimeUtilsTests {

	/*
	 * Berlin - UTC+01, UTC+02
	 */
	private String testISOTime_01 = "2016-07-16T21:40:06+02:00";
	/*
	 * Sofia - UTC+02, UTC+03
	 */
	private String testISOTime_02 = "2016-07-16T22:40:06+03:00";

	@Test
	public void test_Convert_TimeZone() {
		try {
			assertEquals(testISOTime_02, DateTimeUtils.toTimeZone(testISOTime_01, "Europe/Sofia"));
			assertEquals(testISOTime_01, DateTimeUtils.toTimeZone(testISOTime_02, "Europe/Berlin"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_Modify_Date_Layout() {
		try {
			assertEquals("2016-07-16 21:40:06", DateTimeUtils.removeTimeZone(testISOTime_01, "Europe/Berlin"));
			assertEquals("2016-07-16 22:40:06", DateTimeUtils.removeTimeZone(testISOTime_02, "Europe/Sofia"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
