package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FactorInSourceTests {

	@Test
	public void test_factorin_1() {
		try {
			var rates = new Factorin(new NullReporter()).getFactorinRates(TestUtils.getTestResource("/factorin.html"));

			assertEquals("Currencies parsed", 53, rates.size());

			var lastUpdate = TestUtils.newDate(2023, 9, 2, 11, 15);

			assertEquals("EUR", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.95", rates.get(0).getBuy());
			assertEquals("1.962", rates.get(0).getSell());
			assertEquals(lastUpdate, rates.get(0).getDate());

			assertEquals("TRY", rates.get(6).getCode());
			assertEquals(1, rates.get(6).getRatio());
			assertEquals("0.068", rates.get(6).getBuy());
			assertEquals("0.0708", rates.get(6).getSell());
			assertEquals(lastUpdate, rates.get(6).getDate());

			rates.stream().filter(r -> r.getCode().equals("PHP")).findAny().ifPresentOrElse(found -> {
				assertEquals("PHP", found.getCode());
				assertEquals(1, found.getRatio());
				assertEquals("0.0303", found.getBuy());
				assertEquals("0.0331", found.getSell());
				assertEquals(lastUpdate, found.getDate());
			}, () -> fail("PHP not found"));

			rates.stream().filter(r -> r.getCode().equals("EGP")).findAny().ifPresentOrElse(found -> {
				assertEquals("EGP", found.getCode());
				assertEquals(1, found.getRatio());
				assertEquals("0.0601", found.getBuy());
				assertEquals("0.0628", found.getSell());
				assertEquals(lastUpdate, found.getDate());
			}, () -> fail("EGP not found"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}