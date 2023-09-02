package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class AllianzSourceTest {

	@Test
	public void test_Allianz() {
		try {
			var rates = new AllianzSource(new ConsoleReporter()).getAllianzRates(
					TestUtils.getTestResource("/allianz.xml"));

			assertEquals(7, rates.size());

			assertEquals("USD", rates.get(0).getCode());
			assertEquals(1, rates.get(0).getRatio());
			assertEquals("1.7902", rates.get(0).getBuy());
			assertEquals("1.8392", rates.get(0).getSell());
			assertNotEquals("USD date time", 0,
					rates.get(0).getDate().getHours() + rates.get(0).getDate().getMinutes() + rates.get(0).getDate()
							.getSeconds());

			assertEquals("GBP", rates.get(1).getCode());
			assertEquals(1, rates.get(1).getRatio());
			assertEquals("2.2498", rates.get(1).getBuy());
			assertEquals("2.3088", rates.get(1).getSell());

			//            assertEquals("EUR", rates.get(2).getCode());
			//            assertEquals(1, rates.get(2).getRatio());
			//            assertEquals("1.95", rates.get(2).getBuy());
			//            assertEquals("1.959", rates.get(2).getSell());

			assertEquals("JPY", rates.get(3).getCode());
			assertEquals(100, rates.get(3).getRatio());
			assertEquals("1.2125", rates.get(3).getBuy());
			assertEquals("1.2715", rates.get(3).getSell());
			assertNotEquals("JPY date time", 0,
					rates.get(3).getDate().getHours() + rates.get(3).getDate().getMinutes() + rates.get(3).getDate()
							.getSeconds());

			assertEquals("DKK", rates.get(6).getCode());
			assertEquals(10, rates.get(6).getRatio());
			assertEquals("2.5945", rates.get(6).getBuy());
			assertEquals("2.6535", rates.get(6).getSell());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
