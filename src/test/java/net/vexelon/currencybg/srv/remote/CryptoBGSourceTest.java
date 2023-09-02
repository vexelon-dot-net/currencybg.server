package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class CryptoBGSourceTest {

	@Test
	public void test_CryptoBgSource_Header() {
		try {
			var rates = new CryptoBGSource(new ConsoleReporter()).getCryptoRates(
					TestUtils.getTestResource("/crypto_bg_source_header.html"));

			assertEquals("Parsed cryptos", 2, rates.size());

			assertEquals("code is BTC", "BTC", rates.get(0).getCode());
			assertEquals("BTC rate", 1, rates.get(0).getRatio());
			assertEquals("BTC buy", "45073.61", rates.get(0).getBuy());
			assertEquals("BTC sell", "47814.28", rates.get(0).getSell());

			assertEquals("code is ETH", "ETH", rates.get(1).getCode());
			assertEquals("ETH rate", 1, rates.get(1).getRatio());
			assertEquals("ETH buy", "2863.43", rates.get(1).getBuy());
			assertEquals("ETH sell", "3023.22", rates.get(1).getSell());

			var ratesNoData = new CryptoBGSource(new NullReporter()).getCryptoRates(
					TestUtils.getTestResource("/crypto_bg_source_header_no_data.html"));
			assertTrue(ratesNoData.isEmpty());

			var ratesEmpty = new CryptoBGSource(new NullReporter()).getCryptoRates(
					TestUtils.getTestResource("/crypto_bg_source_header_empty.html"));
			assertTrue(ratesEmpty.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
