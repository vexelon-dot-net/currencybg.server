package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class SocieteGeneraleTest {

	@Test
	public void test_Societe01() {
		try {
			List<CurrencyData> rates = new SocieteGenerale(new NullReporter())
			        .getSocieteGeneraleRates(TestUtils.getTestResource("/societe_generale.html"));

			int last = rates.size() - 1;

			assertTrue(rates.get(0).getCode().equals("EUR"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.951000"));
			assertTrue(rates.get(0).getSell().equals("1.959000"));

			assertTrue(rates.get(last).getCode().equals("AUD"));
			assertTrue(rates.get(last).getRatio() == 1);
			assertTrue(rates.get(last).getBuy().equals("1.295000"));
			assertTrue(rates.get(last).getSell().equals("1.345000"));

			assertTrue(rates.get(last - 3).getCode().equals("RON"));
			assertTrue(rates.get(last - 3).getRatio() == 1);
			assertTrue(rates.get(last - 3).getBuy().equals("0.424000"));
			assertTrue(rates.get(last - 3).getSell().equals("0.434000"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
