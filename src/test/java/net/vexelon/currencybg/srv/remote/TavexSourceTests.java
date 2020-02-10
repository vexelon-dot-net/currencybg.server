package net.vexelon.currencybg.srv.remote;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TavexSourceTests {

    @Test
    public void test_tavex_1() {
        try {
            List<CurrencyData> rates = new TavexSource(new NullReporter())
                    .getTavexRates(TestUtils.getTestResource("/tavex.html"));

            int last = rates.size() - 1;

            assertEquals("EUR", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.9500", rates.get(0).getBuy());
            assertEquals("1.9560", rates.get(0).getSell());

            assertEquals("AZN", rates.get(last).getCode());
            assertEquals(1, rates.get(last).getRatio());
            assertEquals("0.957", rates.get(last).getBuy());
            assertEquals("1.158", rates.get(last).getSell());

            assertEquals("VND", rates.get(last - 3).getCode());
            assertEquals(1, rates.get(last - 3).getRatio());
            assertEquals("0.0000690", rates.get(last - 3).getBuy());
            assertEquals("0.0000880", rates.get(last - 3).getSell());

            assertEquals("EEK", rates.get(24).getCode());
            assertEquals(1, rates.get(24).getRatio());
            assertEquals("0.118", rates.get(24).getBuy());
            assertEquals("", rates.get(24).getSell());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
