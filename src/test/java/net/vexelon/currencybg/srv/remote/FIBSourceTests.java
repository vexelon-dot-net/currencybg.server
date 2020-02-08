package net.vexelon.currencybg.srv.remote;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class FIBSourceTests {

    @Test
    public void test_FIB01() {
        try {
            List<CurrencyData> rates = new FIBSource(new NullReporter())
                    .getFIBRates(TestUtils.getTestResource("/fib.html"));

            int last = rates.size() - 1;
            Date lastUpdate = TestUtils.newDate(2020, 2, 7, 18, 03);

            assertEquals("CAD", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.31600", rates.get(0).getBuy());
            assertEquals("1.36730", rates.get(0).getSell());
            assertEquals(lastUpdate, rates.get(0).getDate());

            assertEquals("NOK", rates.get(last).getCode());
            assertEquals(10, rates.get(last).getRatio());
            assertEquals("1.88590", rates.get(last).getBuy());
            assertEquals("1.95920", rates.get(last).getSell());
            assertEquals(lastUpdate, rates.get(last).getDate());

            assertEquals("AUD", rates.get(last - 1).getCode());
            assertEquals(1, rates.get(last - 1).getRatio());
            assertEquals("1.16940", rates.get(last - 1).getBuy());
            assertEquals("1.21480", rates.get(last - 1).getSell());
            assertEquals(lastUpdate, rates.get(last - 1).getDate());

            assertEquals("JPY", rates.get(last - 2).getCode());
            assertEquals(100, rates.get(last - 2).getRatio());
            assertEquals("1.59630", rates.get(last - 2).getBuy());
            assertEquals("1.65850", rates.get(last - 2).getSell());
            assertEquals(lastUpdate, rates.get(last - 2).getDate());

            assertEquals("Expected/Actual", 13, rates.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
