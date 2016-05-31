package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class TavexSourceTests {

	@Test
	public void test_Tavex01() {
		try {
			List<CurrencyData> rates = new TavexSource(new NullReporter())
					.getTavexRates(TestUtils.getTestResource("/tavex01.html"));

			assertTrue(rates.get(0).getCode().equals("AED"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("0.4640"));
			assertTrue(rates.get(0).getSell().equals("0.4930"));
			// TODO
			// assertTrue(rates.get(0).getDate().equals(TestUtils.newDate(2016,
			// 5, 31))); // 31.05.2016

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
