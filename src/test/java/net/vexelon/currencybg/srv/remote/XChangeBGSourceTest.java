package net.vexelon.currencybg.srv.remote;

import com.google.common.collect.Iterables;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class XChangeBGSourceTest {

	@Test
	public void test_xchangebg() {
		var source = new XChangeBGSource(new ConsoleReporter());

		try {
			var rates = source.getXChangeRates(TestUtils.getTestResource("/xchg-bg-exchange-pairs.json"));

			assertEquals("Parsed crypto", 8, rates.size());

			var rate = rates.iterator().next();
			assertEquals("BTC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("45530.14875154", rate.getBuy());
			assertEquals("48346.64805934", rate.getSell());

			rate = Iterables.get(rates, 1);
			assertEquals("ETH", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("2884.21225556", rate.getBuy());
			assertEquals("3062.63728203", rate.getSell());

			rate = Iterables.get(rates, 2);
			assertEquals("DOGE", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("0.11296142", rate.getBuy());
			assertEquals("0.12000733", rate.getSell());

			rate = Iterables.get(rates, 3);
			assertEquals("USDT", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("1.76508152", rate.getBuy());
			assertEquals("1.87486615", rate.getSell());

			rate = Iterables.get(rates, 4);
			assertEquals("LTC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("110.92467365", rate.getBuy());
			assertEquals("117.84642772", rate.getSell());

			rate = Iterables.get(rates, 5);
			assertEquals("BCH", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("341.23518126", rate.getBuy());
			assertEquals("362.56410362", rate.getSell());

			rate = Iterables.get(rates, 6);
			assertEquals("LINK", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("10.75388816", rate.getBuy());
			assertEquals("11.42586586", rate.getSell());

			rate = Iterables.get(rates, 7);
			assertEquals("USDC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("1.76640950", rate.getBuy());
			assertEquals("1.87587338", rate.getSell());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
