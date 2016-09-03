package net.vexelon.currencybg.srv.db.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.tests.TestUtils;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class TestSourceUpdateInfo {

	private static Gson gson;
	private static Type type;

	@BeforeClass
	public static void before() {
		type = new TypeToken<SourceUpdateRestrictions>() {
		}.getType();

		gson = new Gson();
	}

	private Calendar toCal(String value) throws ParseException {
		return new DateTimeUtils(TimeZone.getDefault()).getCalToday(value, Defs.DATETIME_RESTR_FORMAT);
	}

	@Test
	public void test_Parse01() {
		try {
			SourceUpdateRestrictions updateInfo = gson
					.fromJson(new InputStreamReader(TestUtils.getTestResource("/sourceupdateinfo_01.json")), type);

			assertEquals("09:00", updateInfo.getWeekdaysNotBefore());
			assertTrue(toCal(updateInfo.getWeekdaysNotBefore()).getTime().equals(TestUtils.newDate(9, 0)));
			assertEquals("18:00", updateInfo.getWeekdaysNotAfter());
			assertTrue(toCal(updateInfo.getWeekdaysNotAfter()).getTime().equals(TestUtils.newDate(18, 0)));
			assertTrue(toCal(updateInfo.getWeekdaysNotAfter()).equals(TestUtils.newCal(18, 0)));
			assertTrue(toCal(updateInfo.getWeekendsNotBefore()).getTime().equals(TestUtils.newDate(12, 0)));
			assertTrue(toCal(updateInfo.getWeekendsNotAfter()).getTime().equals(TestUtils.newDate(23, 0)));

			assertTrue(updateInfo.isEnabledOnSunday());
			assertTrue(updateInfo.isEnabledOnWeekends());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test_Parse02() {
		try {
			SourceUpdateRestrictions updateInfo = gson
					.fromJson(new InputStreamReader(TestUtils.getTestResource("/sourceupdateinfo_02.json")), type);

			assertEquals("1:13", updateInfo.getWeekdaysNotBefore());
			assertTrue(toCal(updateInfo.getWeekdaysNotBefore()).equals(TestUtils.newCal(1, 13)));
			assertEquals("23:59", updateInfo.getWeekdaysNotAfter());
			assertTrue(toCal(updateInfo.getWeekdaysNotAfter()).equals(TestUtils.newCal(23, 59)));

			assertTrue(updateInfo.isEnabledOnSunday());
			assertFalse(updateInfo.isEnabledOnWeekends());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
