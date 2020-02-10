package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
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
            final List<CurrencyData> rates = new CryptoBGSource(new NullReporter())
                    .getCryptoRates(TestUtils.getTestResource("/crypto_bg_source_header.html"));

            assertEquals("code is BTC", "BTC", rates.get(0).getCode());
            assertEquals("BTC rate", 1, rates.get(0).getRatio());
            assertEquals("BTC buy", "20371.73", rates.get(0).getBuy());
            assertEquals("BTC sell", "21410.27", rates.get(0).getSell());

            final List<CurrencyData> ratesNoData = new CryptoBGSource(new NullReporter())
                    .getCryptoRates(TestUtils.getTestResource("/crypto_bg_source_header_no_data.html"));

            assertTrue(ratesNoData.isEmpty());

            final List<CurrencyData> ratesEmpty = new CryptoBGSource(new NullReporter())
                    .getCryptoRates(TestUtils.getTestResource("/crypto_bg_source_header_empty.html"));

            assertTrue(ratesEmpty.isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
