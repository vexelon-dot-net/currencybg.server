package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class ChangepartnerSourceTest {
	@Test
	public void test_ChangePartnerSource_Header() {

		try {
			List<CurrencyData> rates = new ChangepartnerSource(new NullReporter())
			        .getChangepartnerRates(TestUtils.getTestResource("/changepartner_01.html"));

			assertTrue(rates.get(0).getCode().equals("USD"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("1.690"));
			assertTrue(rates.get(0).getSell().equals("1.700"));
			// CurrencyData [code=USD, ratio=1, buy=1.690, sell=1.700, date=Wed
			// Jul 19 20:57:00 EEST 2017, source=800]

			assertTrue(rates.get(9).getCode().equals("CZK"));
			assertTrue(rates.get(9).getRatio() == 100);
			assertTrue(rates.get(9).getBuy().equals("7.300"));
			assertTrue(rates.get(9).getSell().equals("7.480"));
			// CurrencyData [code=CZK, ratio=100, buy=7.300, sell=7.480,
			// date=Wed Jul 19 20:57:00 EEST 2017, source=800] ->9

			assertTrue(rates.get(19).getCode().equals("HRK"));
			assertTrue(rates.get(19).getRatio() == 100);
			assertTrue(rates.get(19).getBuy().equals("26.30"));
			assertTrue(rates.get(19).getSell().equals("27.30"));
			// CurrencyData [code=HRK, ratio=100, buy=26.30, sell=27.30,
			// date=Wed Jul 19 20:57:00 EEST 2017, source=800] ->19

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
