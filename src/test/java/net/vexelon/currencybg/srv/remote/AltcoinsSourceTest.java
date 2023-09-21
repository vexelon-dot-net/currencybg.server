package net.vexelon.currencybg.srv.remote;

import com.google.common.collect.Iterables;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AltcoinsSourceTest {

	@Test
	public void test_AltcoinsSource01() {
		try {
			var rates = new AltcoinsSource(Vertx.vertx(), new ConsoleReporter()).getAltcoinsRates(
					TestUtils.getTestResource("/altcoins.json"));

			assertEquals("Parsed crypto", 14, rates.size());

			var rate = rates.iterator().next();
			assertEquals("BTC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("45984.868176", rate.getBuy());
			assertEquals("47868.08480999999", rate.getSell());

			rate = Iterables.get(rates, 1);
			assertEquals("ETH", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("2908.809696", rate.getBuy());
			assertEquals("3028.2716772", rate.getSell());

			rate = Iterables.get(rates, 2);
			assertEquals("USDT", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("1.781469768", rate.getBuy());
			assertEquals("1.8542460636", rate.getSell());

			rate = Iterables.get(rates, 3);
			assertEquals("USDC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("1.7766442199999999", rate.getBuy());
			assertEquals("1.8498009599999998", rate.getSell());

			rate = Iterables.get(rates, 4);
			assertEquals("BNB", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("380.49062999999995", rate.getBuy());
			assertEquals("396.272016", rate.getSell());

			rate = Iterables.get(rates, 5);
			assertEquals("XRP", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("0.8836463999999999", rate.getBuy());
			assertEquals("0.92463", rate.getSell());

			rate = Iterables.get(rates, 9);
			assertEquals("XLM", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("0.19804004999999997", rate.getBuy());
			assertEquals("0.21177211999999998", rate.getSell());

			rate = Iterables.get(rates, 13);
			assertEquals("DASH", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("44.083299", rate.getBuy());
			assertEquals("47.098604", rate.getSell());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
