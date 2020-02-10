package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class BitcoinHouseSourceTest {

    @Test
    public void test_bitcoinhouse_1() {

        try {
            List<CurrencyData> rates = new BitcoinsHouseSource(new NullReporter())
                    .getBitcoinsHouseRates(TestUtils.getTestResource("/bitcoinhouse_01.json"));

            assertEquals(4, rates.size());

            assertEquals("BTC", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("23889.5", rates.get(0).getBuy());
            assertEquals("25189.45", rates.get(0).getSell());

            assertEquals("ETH", rates.get(1).getCode());
            assertEquals(1, rates.get(1).getRatio());
            assertEquals("694.75", rates.get(1).getBuy());
            assertEquals("744.75", rates.get(1).getSell());

            assertEquals("LTC", rates.get(2).getCode());
            assertEquals(1, rates.get(2).getRatio());
            assertEquals("207.94", rates.get(2).getBuy());
            assertEquals("247.94", rates.get(2).getSell());

            assertEquals("ZEC", rates.get(3).getCode());
            assertEquals(1, rates.get(3).getRatio());
            assertEquals("467.08", rates.get(3).getBuy());
            assertEquals("517.08", rates.get(3).getSell());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
