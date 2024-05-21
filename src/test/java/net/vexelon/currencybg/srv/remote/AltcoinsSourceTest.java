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

			assertEquals("Parsed crypto", 15, rates.size());

			var rate = rates.iterator().next();
			assertEquals("BTC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("121106.12898149999", rate.getBuy());
			assertEquals("126323.1321744", rate.getSell());

			rate = Iterables.get(rates, 1);
			assertEquals("ETH", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("6511.751493", rate.getBuy());
			assertEquals("6786.5857696", rate.getSell());

			rate = Iterables.get(rates, 2);
			assertEquals("USDT", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("1.739073882", rate.getBuy());
			assertEquals("1.8119137680000001", rate.getSell());

			rate = Iterables.get(rates, 3);
			assertEquals("BNB", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("1065.64185", rate.getBuy());
			assertEquals("1110.572848", rate.getSell());

			rate = Iterables.get(rates, 5);
			assertEquals("XRP", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("0.9379188", rate.getBuy());
			assertEquals("0.9816072", rate.getSell());

			rate = Iterables.get(rates, 9);
			assertEquals("LTC", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("152.4691545", rate.getBuy());
			assertEquals("160.72376319999998", rate.getSell());

			rate = Iterables.get(rates, 13);
			assertEquals("DASH", rate.getCode());
			assertEquals(1, rate.getRatio());
			assertEquals("53.83398149999999", rate.getBuy());
			assertEquals("57.537818800000004", rate.getSell());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
