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

            assertEquals(8, rates.size());

            assertEquals("CHF", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.7994", rates.get(0).getBuy());
            assertEquals("1.8544", rates.get(0).getSell());

            assertEquals("DKK", rates.get(1).getCode());
            assertEquals(10, rates.get(1).getRatio());
            assertEquals("2.5875", rates.get(1).getBuy());
            assertEquals("2.6465", rates.get(1).getSell());

            assertEquals("EUR", rates.get(2).getCode());
            assertEquals(1, rates.get(2).getRatio());
            assertEquals("1.95", rates.get(2).getBuy());
            assertEquals("1.959", rates.get(2).getSell());

            assertEquals("JPY", rates.get(4).getCode());
            assertEquals(100, rates.get(4).getRatio());
            assertEquals("1.5965", rates.get(4).getBuy());
            assertEquals("1.6555", rates.get(4).getSell());

            assertEquals("USD", rates.get(7).getCode());
            assertEquals(1, rates.get(7).getRatio());
            assertEquals("1.761", rates.get(7).getBuy());
            assertEquals("1.81", rates.get(7).getSell());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
