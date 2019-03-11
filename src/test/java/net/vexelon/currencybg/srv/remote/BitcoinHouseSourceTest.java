package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BitcoinHouseSourceTest {

    @Test
    public void test_BitcoinHouse01() {

        try {
            List<CurrencyData> rates = new BitcoinsHouseSource(new NullReporter())
                    .getBitcoinsHouseRates(TestUtils.getTestResource("/bitcoinhouse_01.json"));

            assertTrue(rates.size() == 4);

            assertTrue(rates.get(0).getCode().equals("BTC"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("23889.5"));
            assertTrue(rates.get(0).getSell().equals("25189.45"));

            assertTrue(rates.get(1).getCode().equals("ETH"));
            assertTrue(rates.get(1).getRatio() == 1);
            assertTrue(rates.get(1).getBuy().equals("694.75"));
            assertTrue(rates.get(1).getSell().equals("744.75"));

            assertTrue(rates.get(2).getCode().equals("LTC"));
            assertTrue(rates.get(2).getRatio() == 1);
            assertTrue(rates.get(2).getBuy().equals("207.94"));
            assertTrue(rates.get(2).getSell().equals("247.94"));

            assertTrue(rates.get(3).getCode().equals("ZEC"));
            assertTrue(rates.get(3).getRatio() == 1);
            assertTrue(rates.get(3).getBuy().equals("467.08"));
            assertTrue(rates.get(3).getSell().equals("517.08"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
