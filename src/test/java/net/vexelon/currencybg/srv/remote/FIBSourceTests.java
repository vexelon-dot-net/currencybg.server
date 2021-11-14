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
    public void test_FIB() {
        try {
            List<CurrencyData> rates = new FIBSource(new NullReporter())
                    .getFIBRates(TestUtils.getTestResource("/fib.html"));

            int last = rates.size() - 1;
            Date lastUpdate = TestUtils.newDate(2021, 11, 12, 17, 52);

            assertEquals("AUD", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.22520", rates.get(0).getBuy());
            assertEquals("1.27270", rates.get(0).getSell());
            assertEquals(lastUpdate, rates.get(0).getDate());

            assertEquals("SEK", rates.get(last - 2).getCode());
            assertEquals(10, rates.get(last - 2).getRatio());
            assertEquals("1.91770", rates.get(last - 2).getBuy());
            assertEquals("1.99190", rates.get(last - 2).getSell());
            assertEquals(lastUpdate, rates.get(last - 2).getDate());

            assertEquals("USD", rates.get(last - 1).getCode());
            assertEquals(1, rates.get(last - 1).getRatio());
            assertEquals("1.68450", rates.get(last - 1).getBuy());
            assertEquals("1.73260", rates.get(last - 1).getSell());
            assertEquals(lastUpdate, rates.get(last - 1).getDate());

            assertEquals("XAU", rates.get(last).getCode());
            assertEquals(1, rates.get(last).getRatio());
            assertEquals("3129.97540", rates.get(last).getBuy());
            assertEquals("3258.99780", rates.get(last).getSell());
            assertEquals(lastUpdate, rates.get(last).getDate());

            assertEquals("Expected/Actual", 14, rates.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
