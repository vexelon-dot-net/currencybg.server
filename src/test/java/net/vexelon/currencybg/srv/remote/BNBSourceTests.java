package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class BNBSourceTests {

	@Test
	public void test_BNB01() {
		try {
			List<CurrencyData> rates = new BNBSource(new NullReporter())
					.getBNBRates(TestUtils.getTestResource("/exchange_rates_en.xml"));

			assertTrue(rates.get(0).getCode().equals("AUD"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("0.760647"));
			assertTrue(rates.get(0).getSell().equals("1.31467"));
			assertTrue(rates.get(0).getDate().equals(TestUtils.newDate(2016, 3, 15))); // 15.03.2016

			assertTrue(rates.get(3).getCode().equals("CHF"));
			assertTrue(rates.get(3).getRatio() == 1);
			assertTrue(rates.get(3).getBuy().equals("0.56007"));
			assertTrue(rates.get(3).getSell().equals("1.78549"));
			assertTrue(rates.get(3).getDate().equals(TestUtils.newDate(2016, 3, 15))); // 15.03.2016

			assertTrue(rates.get(14).getCode().equals("ISK"));
			assertTrue(rates.get(14).getRatio() == 0);
			assertTrue(rates.get(14).getSell().equals("0"));
			assertTrue(rates.get(14).getBuy().equals("0"));
			assertTrue(rates.get(14).getDate().equals(TestUtils.newDate(2016, 3, 15))); // 15.03.2016

			int pos = rates.size() - 1;
			assertTrue(rates.get(pos).getCode().equals("XAU"));
			assertTrue(rates.get(pos).getRatio() == 1);
			assertTrue(rates.get(pos).getBuy().equals("0"));
			assertTrue(rates.get(pos).getSell().equals("2175.97"));
			assertTrue(rates.get(pos).getDate().equals(TestUtils.newDate(2016, 3, 15))); // 15.03.2016

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
