package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Deprecated
public class BNBSourceTests {

	@Test
	public void test_bnb_1() {
		try {
			var rates = new BNBSource(new ConsoleReporter()).getBNBRates(
					TestUtils.getTestResource("/exchange_rates_en.xml"));

			assertEquals("AUD", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("0.855747", rates.get(0).getBuy());
			assertEquals("1.16857", rates.get(0).getSell());
			assertEquals(TestUtils.newDate(2023, 9, 1), rates.get(0).getDate()); // 01.09.2023

			assertEquals("CHF", rates.get(3).getCode());
			assertEquals(1, rates.get(3).getRatio());
			assertEquals("0.48951", rates.get(3).getBuy());
			assertEquals("2.04286", rates.get(3).getSell());
			assertEquals(TestUtils.newDate(2023, 9, 1), rates.get(3).getDate());

			assertEquals("ISK", rates.get(13).getCode());
			assertEquals(100, rates.get(13).getRatio());
			assertEquals("1.37251", rates.get(13).getSell());
			assertEquals("72.8592", rates.get(13).getBuy());
			assertEquals(TestUtils.newDate(2023, 9, 1), rates.get(13).getDate());

			int pos = rates.size() - 1;
			assertEquals("XAU", rates.get(pos).getCode());
			assertEquals(1, rates.get(pos).getRatio());
			assertEquals("0", rates.get(pos).getBuy());
			assertEquals("3502.38", rates.get(pos).getSell());
			assertEquals(TestUtils.newDate(2023, 9, 1), rates.get(pos).getDate());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
