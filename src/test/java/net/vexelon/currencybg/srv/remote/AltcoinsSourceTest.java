package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.ConsoleReporter;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AltcoinsSourceTest {

    @Test
    public void test_AltcoinsSource01() {

        try {
            List<CurrencyData> rates = new AltcoinsSource(new ConsoleReporter())
                    .getAltcoinsRates(TestUtils.getTestResource("/altcoins_01.json"));

            // XXX buy/sell rates - size of column is 10!
            // This is not good, because it strips rate infos
            // and the tests should be modified once the db and backend are updated!

            assertTrue(rates.get(0).getCode().equals("BTC"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("17765.2706"));
            assertTrue(rates.get(0).getSell().equals("16948.2768"));

            assertTrue(rates.get(1).getCode().equals("ETH"));
            assertTrue(rates.get(1).getRatio() == 1);
            assertTrue(rates.get(1).getBuy().equals("1398.55835"));
            assertTrue(rates.get(1).getSell().equals("1327.39882"));

            assertTrue(rates.get(2).getCode().equals("LTC"));
            assertTrue(rates.get(2).getRatio() == 1);
            assertTrue(rates.get(2).getBuy().equals("338.401056"));
            assertTrue(rates.get(2).getSell().equals("317.414944"));

            assertTrue(rates.get(3).getCode().equals("XRP"));
            assertTrue(rates.get(3).getRatio() == 1);
            assertTrue(rates.get(3).getBuy().equals("1.51893336"));
            assertTrue(rates.get(3).getSell().equals("1.34697864"));

            assertTrue(rates.get(4).getCode().equals("BCH"));
            assertTrue(rates.get(4).getRatio() == 1);
            assertTrue(rates.get(4).getBuy().equals("2071.22608"));
            assertTrue(rates.get(4).getSell().equals("1978.00064"));

            assertTrue(rates.get(5).getCode().equals("DASH"));
            assertTrue(rates.get(5).getRatio() == 1);
            assertTrue(rates.get(5).getBuy().equals("985.474764"));
            assertTrue(rates.get(5).getSell().equals("939.235479"));

            assertTrue(rates.get(6).getCode().equals("XMR"));
            assertTrue(rates.get(6).getRatio() == 1);
            assertTrue(rates.get(6).getBuy().equals("523.603808"));
            assertTrue(rates.get(6).getSell().equals("483.326592"));

            assertTrue(rates.get(7).getCode().equals("ETC"));
            assertTrue(rates.get(7).getRatio() == 1);
            assertTrue(rates.get(7).getBuy().equals("52.7211776"));
            assertTrue(rates.get(7).getSell().equals("46.7527424"));

            assertTrue(rates.get(8).getCode().equals("ZEC"));
            assertTrue(rates.get(8).getRatio() == 1);
            assertTrue(rates.get(8).getBuy().equals("635.509929"));
            assertTrue(rates.get(8).getSell().equals("586.624550"));

            assertTrue(rates.get(9).getCode().equals("XLM"));
            assertTrue(rates.get(9).getRatio() == 1);
            assertTrue(rates.get(9).getBuy().equals("0.54703000"));
            assertTrue(rates.get(9).getSell().equals("0.48510207"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
