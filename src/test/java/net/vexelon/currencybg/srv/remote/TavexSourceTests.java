package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TavexSourceTests {

	@Test
	public void test_tavex_1() {
		try {
			var rates = new TavexSource(new ConsoleReporter()).getTavexRates(TestUtils.getTestResource("/tavex.html"));

			assertEquals("Currencies parsed", 59, rates.size());

			int last = rates.size() - 1;

			assertEquals("EUR", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.95", rates.get(0).getBuy());
			assertEquals("1.965", rates.get(0).getSell());

			assertEquals("AED", rates.get(7).getCode());
			assertEquals(10, rates.get(7).getRatio());
			assertEquals("4.74", rates.get(7).getBuy());
			assertEquals("5.14", rates.get(7).getSell());

			assertEquals("ALL", rates.get(8).getCode());
			assertEquals(100, rates.get(8).getRatio());
			assertEquals("1.83", rates.get(8).getBuy());
			assertEquals("1.98", rates.get(8).getSell());

			assertTrue("BYN has no rates", rates.stream().noneMatch(r -> "BYN".equals(r.getCode()))); // no rates

			assertEquals("KRW", rates.get(last - 22).getCode());
			assertEquals(1000, rates.get(last - 22).getRatio());
			assertEquals("1.334", rates.get(last - 22).getBuy());
			assertEquals("1.437", rates.get(last - 22).getSell());

			assertEquals("LTL", rates.get(last - 21).getCode());
			assertEquals(1, rates.get(last - 21).getRatio());
			assertEquals("0.541", rates.get(last - 21).getBuy());
			assertEquals("", rates.get(last - 21).getSell());

			assertEquals("LTL", rates.get(last - 21).getCode());
			assertEquals(1, rates.get(last - 21).getRatio());
			assertEquals("0.541", rates.get(last - 21).getBuy());
			assertEquals("", rates.get(last - 21).getSell());

			assertEquals("TWD", rates.get(last - 8).getCode());
			assertEquals(1, rates.get(last - 8).getRatio());
			assertEquals("", rates.get(last - 8).getBuy());
			assertEquals("0.0627", rates.get(last - 8).getSell());

			assertEquals("AMD", rates.get(last).getCode());
			assertEquals(1, rates.get(last).getRatio());
			assertEquals("0.0047", rates.get(last).getBuy());
			assertEquals("0.0052", rates.get(last).getSell());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
