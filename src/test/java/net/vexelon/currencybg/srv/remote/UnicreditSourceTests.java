package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UnicreditSourceTests {

	@Test
	public void test_unicredit_1() {
		try {
			List<CurrencyData> rates = new UnicreditSource(new NullReporter()).getUnicreditRates(
					TestUtils.getTestResource("/unicredit.html"));

			assertEquals("Currencies parsed", 17, rates.size());

			int last = rates.size() - 1;
			Date lastUpdate = TestUtils.newDate(2023, 9, 3, 12, 12);

			assertEquals("EUR", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.945000", rates.get(0).getBuy());
			assertEquals("1.960000", rates.get(0).getSell());
			assertEquals(lastUpdate, rates.get(0).getDate());

			assertEquals("HUF", rates.get(last - 7).getCode());
			assertEquals(1000, rates.get(last - 7).getRatio());
			assertEquals("4.848000", rates.get(last - 7).getBuy());
			assertEquals("5.366000", rates.get(last - 7).getSell());
			assertEquals(lastUpdate, rates.get(last - 7).getDate());

			assertEquals("TRY", rates.get(last).getCode());
			assertEquals(100, rates.get(last).getRatio());
			assertEquals("6.410000", rates.get(last).getBuy());
			assertEquals("7.130000", rates.get(last).getSell());
			assertEquals(lastUpdate, rates.get(last).getDate());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}