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

            assertEquals("XRP", rates.get(3).getCode());
            assertEquals(1, rates.get(3).getRatio());
            assertEquals("0.51689747", rates.get(3).getBuy()); // 0.516897472
            assertEquals("0.47432736", rates.get(3).getSell());

            assertEquals("DASH", rates.get(6).getCode());
            assertEquals(1, rates.get(6).getRatio());
            assertEquals("238.840190", rates.get(6).getBuy()); // 238.8401904
            assertEquals("220.929696", rates.get(6).getSell());

            assertEquals("XLM", rates.get(9).getCode());
            assertEquals(1, rates.get(9).getRatio());
            assertEquals("0.13281602", rates.get(9).getBuy()); // 0.1328160288
            assertEquals("0.12159388", rates.get(9).getSell()); // 0.121593888

            assertEquals("Expected vs. Parsed", AltcoinsSource.CURRENCIES.size(), rates.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
