package net.vexelon.currencybg.srv.remote;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AllianzSourceTest {

    @Test
    public void test_Allianz() {
        try {
            List<CurrencyData> rates = new AllianzSource(new NullReporter())
                    .getAllianzRates(TestUtils.getTestResource("/allianz.xml"));

            assertEquals(7, rates.size());

            assertEquals("CHF", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.826", rates.get(0).getBuy());
            assertEquals("1.881", rates.get(0).getSell());
            assertNotEquals("CHF date time", 0, rates.get(0).getDate().getHours() +
                    rates.get(0).getDate().getMinutes() + rates.get(0).getDate().getSeconds());

            assertEquals("DKK", rates.get(1).getCode());
            assertEquals(10, rates.get(1).getRatio());
            assertEquals("2.6005", rates.get(1).getBuy());
            assertEquals("2.6595", rates.get(1).getSell());

//            assertEquals("EUR", rates.get(2).getCode());
//            assertEquals(1, rates.get(2).getRatio());
//            assertEquals("1.95", rates.get(2).getBuy());
//            assertEquals("1.959", rates.get(2).getSell());

            assertEquals("JPY", rates.get(3).getCode());
            assertEquals(100, rates.get(3).getRatio());
            assertEquals("1.4665", rates.get(3).getBuy());
            assertEquals("1.5255", rates.get(3).getSell());
            assertNotEquals("JPY date time", 0, rates.get(3).getDate().getHours() +
                    rates.get(3).getDate().getMinutes() + rates.get(3).getDate().getSeconds());

            assertEquals("USD", rates.get(6).getCode());
            assertEquals(1, rates.get(6).getRatio());
            assertEquals("1.6847", rates.get(6).getBuy());
            assertEquals("1.7337", rates.get(6).getSell());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
