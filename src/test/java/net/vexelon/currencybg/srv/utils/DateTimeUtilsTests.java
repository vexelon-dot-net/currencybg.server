package net.vexelon.currencybg.srv.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.vexelon.currencybg.srv.Defs;

public class DateTimeUtilsTests {

	private String testISOTime_01 = "2016-07-16T21:40:06+0200";
	private String testISOTime_02 = "2016-07-16T22:40:06+0300";

	@Test
	public void test_Convert_TimeZone() {
		try {
			assertEquals(testISOTime_02,
					DateTimeUtils.toTimeZone(testISOTime_01, Defs.DATETIME_ZONE_FORMAT, "Europe/Sofia"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_Modify_Date_Layout() {
		try {
			assertEquals("2016-07-16 21:40:06",
					DateTimeUtils.removeTimeZone(testISOTime_01, Defs.DATETIME_ZONE_FORMAT, "Europe/Berlin"));

			assertEquals("2016-07-16 22:40:06",
					DateTimeUtils.removeTimeZone(testISOTime_02, Defs.DATETIME_ZONE_FORMAT, "Europe/Sofia"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
