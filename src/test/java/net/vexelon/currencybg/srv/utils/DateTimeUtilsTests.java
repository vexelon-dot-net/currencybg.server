package net.vexelon.currencybg.srv.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import org.junit.Test;

import net.vexelon.currencybg.srv.tests.TestUtils;

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

	@Test
	public void test_CalendarTimeZones_NYC_SOF_2() {
		try {
			int startHour = 06;
			int startMinute = 00;

			ZonedDateTime dtSOF = ZonedDateTime.of(2016, 9, 7, startHour, startMinute, 0, 0,
			        ZoneId.of(TestUtils.TIMEZONE_SOFIA));
			ZonedDateTime dtNYC = ZonedDateTime.of(2016, 9, 6, 22, 50, 0, 0, ZoneId.of(TestUtils.TIMEZONE_NEWYORK));

			Duration dur = Duration.between(dtSOF, dtNYC);
			assertTrue(dur.abs().getSeconds() > 0);

			dtNYC = ZonedDateTime.of(2016, 9, 6, 23, 00, 0, 0, ZoneId.of(TestUtils.TIMEZONE_NEWYORK));

			dur = Duration.between(dtSOF, dtNYC);
			assertTrue(dur.getSeconds() == 0);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_CalendarWeekdays() {
		try {
			DateTimeUtils dateTimeUtils = new DateTimeUtils("Europe/Sofia");

			Calendar calFri = dateTimeUtils.getCal(TestUtils.newDate(2016, 8, 26));
			Calendar calSat = dateTimeUtils.getCal(TestUtils.newDate(2016, 8, 27));
			Calendar calSun = dateTimeUtils.getCal(TestUtils.newDate(2016, 8, 28));

			assertTrue("28-08-2016 is sunday", dateTimeUtils.isSunday(calSun.getTime()));
			assertTrue("28-08-2016 is weekend", dateTimeUtils.isWeekend(calSun.getTime()));

			assertTrue("27-08-2016 is saturday", dateTimeUtils.isSaturday(calSat.getTime()));
			assertTrue("27-08-2016 is weekend", dateTimeUtils.isWeekend(calSat.getTime()));

			assertTrue("26-08-2016 is weekday", dateTimeUtils.isWeekday(calFri.getTime()));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
