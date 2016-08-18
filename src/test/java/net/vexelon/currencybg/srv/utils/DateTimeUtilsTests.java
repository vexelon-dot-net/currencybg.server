package net.vexelon.currencybg.srv.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.vexelon.currencybg.srv.Defs;

public class DateTimeUtilsTests {

	// @Test
	// public void test_Convert_TimeZone() {
	// // fail("Not yet implemented");
	// // assertTrue(1 == 1);
	//
	// try {
	// assertTrue(DateTimeUtils
	// .toTimeZone("2016-07-16T21:40:06+0200", Defs.CURRENT_TIME_ZONE,
	// Defs.DATETIME_ZONE_FORMAT)
	// .equals("2016-07-16T22:40:06+0300"));
	// } catch (Exception e) {
	// e.printStackTrace();
	// fail(e.getMessage());
	// }
	// }

	@Test
	public void test_Modify_Date_Layout() {
		try {
			assertTrue(DateTimeUtils
					.modifyDateLayout("2016-07-16T22:40:06+0300", Defs.DATETIME_ZONE_FORMAT, Defs.DATETIME_FORMAT)
					.equals("2016-07-16 22:40:06"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
