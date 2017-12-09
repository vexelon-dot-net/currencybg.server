package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XChangeBGSourceTest {
    @Test
    public void test_CryptoBgSource_Header() {

        try {
            List<CurrencyData> rates = new XChangeBGSource(new NullReporter())
                    .getCryptoRates(TestUtils.getTestResource("/xbg_01.html"));

            assertTrue(rates.get(0).getCode().equals("BTC"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("24198.67"));
            assertTrue(rates.get(0).getSell().equals("22797.00"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
