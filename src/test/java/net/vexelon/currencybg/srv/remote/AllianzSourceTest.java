package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class AllianzSourceTest {
	@Test
	public void test_AllianzSource_Rates01() {
		try {
			List<CurrencyData> rates = new AllianzSource(new NullReporter())
			        .getAllianzRates(TestUtils.getTestResource("/allianz_01.html"));

			assertTrue(rates.get(0).getCode().equals("USD"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.6676"));
			assertTrue(rates.get(0).getSell().equals("1.7376"));

			assertTrue(rates.get(3).getCode().equals("GBP"));
			assertTrue(rates.get(3).getRatio() == 1);
			assertTrue(rates.get(3).getBuy().equals("2.1542"));
			assertTrue(rates.get(3).getSell().equals("2.2242"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
