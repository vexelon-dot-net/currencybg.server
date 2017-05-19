package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class UnicreditSourceTests {

	@Test
	public void test_Unicredit01() {
		try {
			List<CurrencyData> rates = new UnicreditSource(new NullReporter())
			        .getUnicreditRates(TestUtils.getTestResource("/unicredit.html"));

			int last = rates.size() - 1;
			Date lastUpdate = TestUtils.newDate(2017, 05, 10, 21, 02); //
			// 10.05.2017,
			// 21:02

			assertTrue(rates.get(0).getCode().equals("EUR"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.945000"));
			assertTrue(rates.get(0).getSell().equals("1.960000"));
			assertTrue(rates.get(0).getDate().equals(lastUpdate));

			assertTrue(rates.get(last).getCode().equals("TRY"));
			assertTrue(rates.get(last).getRatio() == 10);
			assertTrue(rates.get(last).getBuy().equals("4.760000"));
			assertTrue(rates.get(last).getSell().equals("5.270000"));
			assertTrue(rates.get(last).getDate().equals(lastUpdate));

			assertTrue(rates.get(last - 3).getCode().equals("RSD"));
			assertTrue(rates.get(last - 3).getRatio() == 100);
			assertTrue(rates.get(last - 3).getBuy().equals("1.509000"));
			assertTrue(rates.get(last - 3).getSell().equals("1.667000"));
			assertTrue(rates.get(last - 3).getDate().equals(lastUpdate));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
