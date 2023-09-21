package net.vexelon.currencybg.srv.remote;

import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Polana1SourceTests {

	@Test
	public void test_polana1_1() {
		try {
			var rates = new Polana1(Vertx.vertx(), new NullReporter()).getPolana1(
					TestUtils.getTestResource("/polana1.html"));

			assertEquals("Currencies parsed", 15, rates.size());

			int last = rates.size() - 1;
			var lastUpdate = TestUtils.newDate(2023, 9, 2,
					LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA)).getHour(),
					LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA)).getMinute());

			assertEquals("EUR", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.95", rates.get(0).getBuy());
			assertEquals("1.962", rates.get(0).getSell());
			assertEquals(lastUpdate, rates.get(0).getDate());

			assertEquals("RSD", rates.get(last - 6).getCode());
			assertEquals(100, rates.get(last - 6).getRatio());
			assertEquals("1.67", rates.get(last - 6).getBuy());
			assertEquals("1.75", rates.get(last - 6).getSell());
			assertEquals(lastUpdate, rates.get(last - 6).getDate());

			assertEquals("HUF", rates.get(last - 2).getCode());
			assertEquals(100, rates.get(last - 2).getRatio());
			assertEquals("0.505", rates.get(last - 2).getBuy());
			assertEquals("0.535", rates.get(last - 2).getSell());
			assertEquals(lastUpdate, rates.get(last - 2).getDate());

			assertEquals("RUB", rates.get(last - 1).getCode());
			assertEquals(100, rates.get(last - 1).getRatio());
			assertEquals("1.70", rates.get(last - 1).getBuy());
			assertEquals("2.00", rates.get(last - 1).getSell());
			assertEquals(lastUpdate, rates.get(last - 1).getDate());

			assertEquals("PLN", rates.get(last).getCode());
			assertEquals(1, rates.get(last).getRatio());
			assertEquals("0.425", rates.get(last).getBuy());
			assertEquals("0.445", rates.get(last).getSell());
			assertEquals(lastUpdate, rates.get(last).getDate());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
