package net.vexelon.currencybg.srv.remote;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ForexHouseSourceTest {
    @Test
    public void test_forexhouse_1() {

        try {
            List<CurrencyData> rates = new ForexHouseSource(new NullReporter())
                    .getForexHouseRates(TestUtils.getTestResource("/forexhouse_01.html"));

            assertEquals("GBP", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("2.180", rates.get(0).getBuy());
            assertEquals("2.220", rates.get(0).getSell());
            // CurrencyData [code=GBP, ratio=1, buy=2.180, sell=2.220, date=Wed
            // Jul 19 21:17:00 EEST 2017, source=900]

            assertEquals("NOK", rates.get(9).getCode());
            assertEquals(1, rates.get(9).getRatio());
            assertEquals("0.195", rates.get(9).getBuy());
            assertEquals("0.215", rates.get(9).getSell());
            // CurrencyData [code=NOK, ratio=1, buy=0.195, sell=0.215, date=Wed
            // Jul 19 21:17:00 EEST 2017, source=900]

            assertEquals("MDL", rates.get(17).getCode());
            assertEquals(1, rates.get(17).getRatio());
            assertEquals("0.070", rates.get(17).getBuy());
            assertEquals("0.100", rates.get(17).getSell());
            // CurrencyData [code=MDL, ratio=1, buy=0.070, sell=0.100, date=Wed
            // Jul 19 21:17:00 EEST 2017, source=900]

            assertEquals("UAH", rates.get(22).getCode());
            assertEquals(1, rates.get(22).getRatio());
            assertEquals("0.050", rates.get(22).getBuy());
            assertEquals("0.080", rates.get(22).getSell());
            // CurrencyData [code=UAH, ratio=1, buy=0.050, sell=0.080, date=Wed
            // Jul 19 21:17:00 EEST 2017, source=900]

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
}
