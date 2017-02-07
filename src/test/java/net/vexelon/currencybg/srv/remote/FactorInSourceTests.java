package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class FactorInSourceTests {

	@Test
	public void test_FactorIn01() {
		try {
			List<CurrencyData> rates = new Factorin(new NullReporter())
			        .getFactorinRates(TestUtils.getTestResource("/factorin.html"));

			int last = rates.size() - 1;
			Date lastUpdate = TestUtils.newDate(2017, 2, 04, 17, 00); // 04.02.2017,
			                                                          // 17:00

			assertTrue(rates.get(0).getCode().equals("EUR"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.955500"));
			assertTrue(rates.get(0).getSell().equals("1.956000"));
			assertTrue(rates.get(0).getDate().equals(lastUpdate));

			assertTrue(rates.get(last).getCode().equals("DOP"));
			assertTrue(rates.get(last).getRatio() == 1);
			assertTrue(rates.get(last).getBuy().equals("0.037500"));
			assertTrue(rates.get(last).getSell().equals("0.037900"));
			assertTrue(rates.get(last).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 18).getCode().equals("THB"));
			assertTrue(rates.get(last - 18).getRatio() == 1);
			assertTrue(rates.get(last - 18).getBuy().equals("0.053300"));
			assertTrue(rates.get(last - 18).getSell().equals("0.053600"));
			assertTrue(rates.get(last - 18).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 19).getCode().equals("CNY"));
			assertTrue(rates.get(last - 19).getRatio() == 1);
			assertTrue(rates.get(last - 19).getBuy().equals("0.267000"));
			assertTrue(rates.get(last - 19).getSell().equals("0.268000"));
			assertTrue(rates.get(last - 19).getDate().equals(lastUpdate));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
