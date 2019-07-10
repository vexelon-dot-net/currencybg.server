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

            assertEquals("code is BTC", rates.get(0).getCode(), "BTC");
            assertEquals("BTC rate", rates.get(0).getRatio(), 1);
            assertEquals("BTC buy", rates.get(0).getBuy(), "20371.73");
            assertEquals("BTC sell", rates.get(0).getSell(), "21410.27");

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
