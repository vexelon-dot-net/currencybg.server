package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AltcoinsSourceTest {

    @Test
    public void test_AltcoinsSource01() {

        try {
            List<CurrencyData> rates = new AltcoinsSource(new ConsoleReporter())
                    .getAltcoinsRates(TestUtils.getTestResource("/altcoins.json"));

            // XXX buy/sell rates - size of column is 10!
            // This is not good, because it strips rate infos
            // and the tests should be modified once the db and backend are updated!

            assertEquals("BTC", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("18057.3255", rates.get(0).getBuy()); // 18057.325552
            assertEquals("17226.7095", rates.get(0).getSell());

            assertEquals("ETH", rates.get(1).getCode());
            assertEquals(1, rates.get(1).getRatio());
            assertEquals("411.74455", rates.get(1).getBuy());
            assertEquals("389.642175", rates.get(1).getSell());

            assertEquals("LTC", rates.get(2).getCode());
            assertEquals(1, rates.get(2).getRatio());
            assertEquals("141.77513", rates.get(2).getBuy());
            assertEquals("133.331835", rates.get(2).getSell());

            assertEquals("XRP", rates.get(3).getCode());
            assertEquals(1, rates.get(3).getRatio());
            assertEquals("0.51689747", rates.get(3).getBuy()); // 0.516897472
            assertEquals("0.47432736", rates.get(3).getSell());

            assertEquals("BCH", rates.get(4).getCode());
            assertEquals(1, rates.get(4).getRatio());
            assertEquals("820.6765", rates.get(4).getBuy());
            assertEquals("776.09025", rates.get(4).getSell());

            assertEquals("DASH", rates.get(5).getCode());
            assertEquals(1, rates.get(5).getRatio());
            assertEquals("238.840190", rates.get(5).getBuy()); // 238.8401904
            assertEquals("220.929696", rates.get(5).getSell());

            assertEquals("XLM", rates.get(8).getCode());
            assertEquals(1, rates.get(8).getRatio());
            assertEquals("0.13281602", rates.get(8).getBuy()); // 0.1328160288
            assertEquals("0.12159388", rates.get(8).getSell()); // 0.121593888

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
