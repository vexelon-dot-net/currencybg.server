package net.vexelon.currencybg.srv.remote;

import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FIBSourceTests {

	@Test
	public void test_FIB() {
		try {
			var rates = new FIBSource(Vertx.vertx(), new NullReporter()).getFIBRates(
					TestUtils.getTestResource("/fib.html"));

			assertEquals("Currencies parsed", 13, rates.size());

			int last = rates.size() - 1;
			var lastUpdate = TestUtils.newDate(2023, 9, 1, 17, 49);

			assertEquals("AUD", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.14710", rates.get(0).getBuy());
			assertEquals("1.19050", rates.get(0).getSell());
			assertEquals(lastUpdate, rates.get(0).getDate());

			assertEquals("JPY", rates.get(last - 5).getCode());
			assertEquals(100, rates.get(last - 5).getRatio());
			assertEquals("1.21460", rates.get(last - 5).getBuy());
			assertEquals("1.27030", rates.get(last - 5).getSell());
			assertEquals(lastUpdate, rates.get(last - 5).getDate());

			assertEquals("SEK", rates.get(last - 2).getCode());
			assertEquals(10, rates.get(last - 2).getRatio());
			assertEquals("1.61300", rates.get(last - 2).getBuy());
			assertEquals("1.67480", rates.get(last - 2).getSell());
			assertEquals(lastUpdate, rates.get(last - 2).getDate());

			assertEquals("USD", rates.get(last - 1).getCode());
			assertEquals(1, rates.get(last - 1).getRatio());
			assertEquals("1.77780", rates.get(last - 1).getBuy());
			assertEquals("1.84040", rates.get(last - 1).getSell());
			assertEquals(lastUpdate, rates.get(last - 1).getDate());

			assertEquals("XAU", rates.get(last).getCode());
			assertEquals(1, rates.get(last).getRatio());
			assertEquals("3449.11000", rates.get(last).getBuy());
			assertEquals("3591.08000", rates.get(last).getSell());
			assertEquals(lastUpdate, rates.get(last).getDate());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
