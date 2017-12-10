package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class FIBSourceTests {

	@Test
	public void test_FIB01() {
		try {
			List<CurrencyData> rates = new FIBSource(new NullReporter())
			        .getFIBRates(TestUtils.getTestResource("/fib01.html"));

			int last = rates.size() - 1;
			Date lastUpdate = TestUtils.newDate(2017, 2, 2, 22, 10); // 02.02.2017,
			                                                         // 22:10

			assertTrue(rates.get(0).getCode().equals("AUD"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.35700"));
			assertTrue(rates.get(0).getSell().equals("1.42490"));
			assertTrue(rates.get(0).getDate().equals(lastUpdate));

			assertTrue(rates.get(last).getCode().equals("RUB"));
			assertTrue(rates.get(last).getRatio() == 100);
			assertTrue(rates.get(last).getBuy().equals("2.68090"));
			assertTrue(rates.get(last).getSell().equals("3.45090"));
			assertTrue(rates.get(last).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 1).getCode().equals("SEK"));
			assertTrue(rates.get(last - 1).getRatio() == 10);
			assertTrue(rates.get(last - 1).getBuy().equals("2.02850"));
			assertTrue(rates.get(last - 1).getSell().equals("2.12940"));
			assertTrue(rates.get(last - 1).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 4).getCode().equals("JPY"));
			assertTrue(rates.get(last - 4).getRatio() == 100);
			assertTrue(rates.get(last - 4).getBuy().equals("1.57210"));
			assertTrue(rates.get(last - 4).getSell().equals("1.65250"));
			assertTrue(rates.get(last - 4).getDate().equals(lastUpdate));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
