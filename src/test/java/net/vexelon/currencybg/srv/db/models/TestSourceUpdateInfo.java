package net.vexelon.currencybg.srv.db.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.vexelon.currencybg.srv.tests.TestUtils;

public class TestSourceUpdateInfo {

	private static Gson gson;
	private static Type type;

	@BeforeClass
	public static void before() {
		type = new TypeToken<SourceUpdateInfo>() {
		}.getType();

		gson = new Gson();
	}

	@Test
	public void test_Parse01() {
		try {
			SourceUpdateInfo updateInfo = gson
					.fromJson(new InputStreamReader(TestUtils.getTestResource("/sourceupdateinfo_01.json")), type);

			assertEquals("09:00", updateInfo.getNotBefore());
			assertTrue(updateInfo.getNotBeforeCalendar().getTime().equals(TestUtils.newDate(9, 0)));
			assertEquals("18:00", updateInfo.getNotAfter());
			assertTrue(updateInfo.getNotAfterCalendar().getTime().equals(TestUtils.newDate(18, 0)));
			assertTrue(updateInfo.getNotAfterCalendar().equals(TestUtils.newCal(18, 0)));

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
			SourceUpdateInfo updateInfo = gson
					.fromJson(new InputStreamReader(TestUtils.getTestResource("/sourceupdateinfo_02.json")), type);

			assertEquals("1:13", updateInfo.getNotBefore());
			assertTrue(updateInfo.getNotBeforeCalendar().equals(TestUtils.newCal(1, 13)));
			assertEquals("23:59", updateInfo.getNotAfter());
			assertTrue(updateInfo.getNotAfterCalendar().equals(TestUtils.newCal(23, 59)));

			assertTrue(updateInfo.isEnabledOnSunday());
			assertFalse(updateInfo.isEnabledOnWeekends());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
