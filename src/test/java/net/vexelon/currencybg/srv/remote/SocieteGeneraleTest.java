package net.vexelon.currencybg.srv.remote;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class SocieteGeneraleTest {

    @Test
    public void test_sg_1() {
        try {
            List<CurrencyData> rates = new SocieteGenerale(new NullReporter())
                    .getSocieteGeneraleRates(TestUtils.getTestResource("/societe_generale.html"));

            int last = rates.size() - 1;

            assertEquals("EUR", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.951000", rates.get(0).getBuy());
            assertEquals("1.960000", rates.get(0).getSell());

            assertEquals("AUD", rates.get(last).getCode());
            assertEquals(1, rates.get(last).getRatio());
            assertEquals("1.164800", rates.get(last).getBuy());
            assertEquals("1.214800", rates.get(last).getSell());

            assertEquals("RON", rates.get(last - 3).getCode());
            assertEquals(1, rates.get(last - 3).getRatio());
            assertEquals("0.406900", rates.get(last - 3).getBuy());
            assertEquals("0.414400", rates.get(last - 3).getSell());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
