package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class ForexHouseSourceTest {
	@Test
	public void test_ForexHouse01() {

		try {
			List<CurrencyData> rates = new ForexHouseSource(new NullReporter())
			        .getForexHouseRates(TestUtils.getTestResource("/forexhouse_01.html"));

			assertTrue(rates.get(0).getCode().equals("GBP"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("2.180"));
			assertTrue(rates.get(0).getSell().equals("2.220"));
			// CurrencyData [code=GBP, ratio=1, buy=2.180, sell=2.220, date=Wed
			// Jul 19 21:17:00 EEST 2017, source=900]

			assertTrue(rates.get(9).getCode().equals("NOK"));
			assertTrue(rates.get(9).getRatio() == 1);
			assertTrue(rates.get(9).getBuy().equals("0.195"));
			assertTrue(rates.get(9).getSell().equals("0.215"));
			// CurrencyData [code=NOK, ratio=1, buy=0.195, sell=0.215, date=Wed
			// Jul 19 21:17:00 EEST 2017, source=900]

			assertTrue(rates.get(17).getCode().equals("MDL"));
			assertTrue(rates.get(17).getRatio() == 1);
			assertTrue(rates.get(17).getBuy().equals("0.070"));
			assertTrue(rates.get(17).getSell().equals("0.100"));
			// CurrencyData [code=MDL, ratio=1, buy=0.070, sell=0.100, date=Wed
			// Jul 19 21:17:00 EEST 2017, source=900]

			assertTrue(rates.get(22).getCode().equals("UAH"));
			assertTrue(rates.get(22).getRatio() == 1);
			assertTrue(rates.get(22).getBuy().equals("0.050"));
			assertTrue(rates.get(22).getSell().equals("0.080"));
			// CurrencyData [code=UAH, ratio=1, buy=0.050, sell=0.080, date=Wed
			// Jul 19 21:17:00 EEST 2017, source=900]

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
