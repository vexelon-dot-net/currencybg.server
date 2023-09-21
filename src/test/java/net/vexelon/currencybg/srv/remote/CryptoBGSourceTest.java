package net.vexelon.currencybg.srv.remote;

import com.google.common.collect.Iterables;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class CryptoBGSourceTest {

	@Test
	public void test_CryptoBgSource_Header() {
		try {
			var rates = new CryptoBGSource(Vertx.vertx(), new NullReporter()).getCryptoRates(
					TestUtils.getTestResource("/crypto_bg_source_header.html"));

			assertEquals("Parsed crypto", 4, rates.size());

			var rate = rates.iterator().next();
			assertEquals("code is BTC", "BTC", rate.getCode());
			assertEquals("BTC rate", 1, rate.getRatio());
			assertEquals("BTC buy", "45073.61", rate.getBuy());
			assertEquals("BTC sell", "47814.28", rate.getSell());

			rate = Iterables.get(rates, 1);
			assertEquals("code is USDC", "USDC", rate.getCode());
			assertEquals("USDC rate", 1, rate.getRatio());
			assertEquals("USDC buy", "1.75", rate.getBuy());
			assertEquals("USDC sell", "1.85", rate.getSell());

			rate = Iterables.get(rates, 2);
			assertEquals("code is USDT", "USDT", rate.getCode());
			assertEquals("USDT rate", 1, rate.getRatio());
			assertEquals("USDT buy", "1.75", rate.getBuy());
			assertEquals("USDT sell", "1.85", rate.getSell());

			rate = Iterables.get(rates, 3);
			assertEquals("code is ETH", "ETH", rate.getCode());
			assertEquals("ETH rate", 1, rate.getRatio());
			assertEquals("ETH buy", "2863.43", rate.getBuy());
			assertEquals("ETH sell", "3023.22", rate.getSell());

			var ratesNoData = new CryptoBGSource(Vertx.vertx(), new NullReporter()).getCryptoRates(
					TestUtils.getTestResource("/crypto_bg_source_header_no_data.html"));
			assertTrue(ratesNoData.isEmpty());

			var ratesEmpty = new CryptoBGSource(Vertx.vertx(), new NullReporter()).getCryptoRates(
					TestUtils.getTestResource("/crypto_bg_source_header_empty.html"));
			assertTrue(ratesEmpty.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
