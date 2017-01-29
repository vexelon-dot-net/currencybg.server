package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
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

			int last = rates.size() - 1;
			Date lastUpdate = TestUtils.newDate(2017, 1, 1, 10, 23); // 29.01.2017,
			                                                         // 10:23

			assertTrue(rates.get(0).getCode().equals("EUR"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.950"));
			assertTrue(rates.get(0).getSell().equals("1.958"));
			assertTrue(rates.get(0).getDate().equals(lastUpdate));

			assertTrue(rates.get(last).getCode().equals("ZAR"));
			assertTrue(rates.get(last).getRatio() == 1);
			assertTrue(rates.get(last).getBuy().equals("0.130"));
			assertTrue(rates.get(last).getSell().equals("0.141"));
			assertTrue(rates.get(last).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 18).getCode().equals("LVL"));
			assertTrue(rates.get(last - 18).getRatio() == 1);
			assertTrue(rates.get(last - 18).getBuy().equals("2.658"));
			assertTrue(rates.get(last - 18).getSell().isEmpty());
			assertTrue(rates.get(last - 18).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 19).getCode().equals("LTL"));
			assertTrue(rates.get(last - 19).getRatio() == 1);
			assertTrue(rates.get(last - 19).getBuy().equals("0.541"));
			assertTrue(rates.get(last - 19).getSell().isEmpty());
			assertTrue(rates.get(last - 19).getDate().equals(lastUpdate));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
