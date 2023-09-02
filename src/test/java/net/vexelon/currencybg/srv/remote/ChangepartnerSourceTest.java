package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChangepartnerSourceTest {

	@Test
	public void test_changepartner_1() {
		try {
			var rates = new ChangepartnerSource(new ConsoleReporter()).getChangepartnerRates(
					TestUtils.getTestResource("/changepartner_01.html"));

			assertEquals("Currencies parsed", 16, rates.size());

			assertEquals("USD", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.790", rates.get(0).getBuy());
			assertEquals("1.811", rates.get(0).getSell());

			assertEquals("NOK", rates.get(6).getCode());
			assertEquals(1, rates.get(6).getRatio());
			assertEquals("", rates.get(6).getBuy());
			assertEquals("0.169", rates.get(6).getSell());

			assertEquals("SEK", rates.get(7).getCode());
			assertEquals(1, rates.get(7).getRatio());
			assertEquals("0.155", rates.get(7).getBuy());
			assertEquals("0.164", rates.get(7).getSell());

			assertEquals("CZK", rates.get(8).getCode());
			assertEquals(1, rates.get(8).getRatio());
			assertEquals("0.0790", rates.get(8).getBuy());
			assertEquals("0.0810", rates.get(8).getSell());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}