package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class XChangeBGSourceTest {
    @Test
    public void test_xchangebg_1() {

        try {
            List<CurrencyData> rates = new XChangeBGSource(new NullReporter())
                    .getXChangeRates(TestUtils.getTestResource("/xbg_01.html"));

            assertEquals("BTC", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("24198.67", rates.get(0).getBuy());
            assertEquals("22797.00", rates.get(0).getSell());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
