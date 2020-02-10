package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Cryptobank.bg is now owned by Altcoins.bg
 */
@Deprecated
public class CryptoBankSourceTest {
    @Test
    public void test_cryptobank_1() {

        try {
            List<CurrencyData> rates = new CryptoBankSource(new NullReporter())
                    .getCryptoBankRates(TestUtils.getTestResource("/cryptobank_01.html"));

            assertTrue(rates.size() == 6);

            assertTrue(rates.get(0).getCode().equals("DOGE"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("0.004227"));
            assertTrue(rates.get(0).getSell().equals("0.004516"));

            assertTrue(rates.get(1).getCode().equals("BCH"));
            assertTrue(rates.get(1).getRatio() == 1);
            assertTrue(rates.get(1).getBuy().equals("2136.64"));
            assertTrue(rates.get(1).getSell().equals("2246.21"));

            assertTrue(rates.get(2).getCode().equals("DASH"));
            assertTrue(rates.get(2).getRatio() == 1);
            assertTrue(rates.get(2).getBuy().equals("1118.46"));
            assertTrue(rates.get(2).getSell().equals("1175.82"));

            assertTrue(rates.get(3).getCode().equals("LTC"));
            assertTrue(rates.get(3).getRatio() == 1);
            assertTrue(rates.get(3).getBuy().equals("229.31"));
            assertTrue(rates.get(3).getSell().equals("241.07"));

            assertTrue(rates.get(4).getCode().equals("ETH"));
            assertTrue(rates.get(4).getRatio() == 1);
            assertTrue(rates.get(4).getBuy().equals("718.61"));
            assertTrue(rates.get(4).getSell().equals("752.41"));

            assertTrue(rates.get(5).getCode().equals("BTC"));
            assertTrue(rates.get(5).getRatio() == 1);
            assertTrue(rates.get(5).getBuy().equals("25200.59"));
            assertTrue(rates.get(5).getSell().equals("26229.18"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
