package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class CryptoBGSourceTest {
	@Test
	public void test_CryptoBgSource_Header() {

		try {
			List<CurrencyData> rates = new CryptoBGSource(new NullReporter())
			        .getCryptoRates(TestUtils.getTestResource("/crypto_bg_source_header.html"));

			assertTrue(rates.get(0).getCode().equals("BTC"));
			assertTrue(rates.get(0).getRatio() == 1);
			assertTrue(rates.get(0).getBuy().equals("4792.00"));
			assertTrue(rates.get(0).getSell().equals("4992.31"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

}
