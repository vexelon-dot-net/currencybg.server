package net.vexelon.currencybg.srv.remote;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class UnicreditSourceTests {

    @Test
    public void test_inicredit_1() {
        try {
            List<CurrencyData> rates = new UnicreditSource(new NullReporter())
                    .getUnicreditRates(TestUtils.getTestResource("/unicredit.html"));

            int last = rates.size() - 1;
            Date lastUpdate = TestUtils.newDate(2020, 02, 8, 21, 38); //

            assertEquals("EUR", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.945000", rates.get(0).getBuy());
            assertEquals("1.960000", rates.get(0).getSell());
            assertEquals(lastUpdate, rates.get(0).getDate());

            assertEquals("TRY", rates.get(last).getCode());
            assertEquals(10, rates.get(last).getRatio());
            assertEquals("2.804000", rates.get(last).getBuy());
            assertEquals("3.101000", rates.get(last).getSell());
            assertEquals(lastUpdate, rates.get(last).getDate());

            assertEquals("RSD", rates.get(last - 3).getCode());
            assertEquals(100, rates.get(last - 3).getRatio());
            assertEquals("1.580000", rates.get(last - 3).getBuy());
            assertEquals("1.746000", rates.get(last - 3).getSell());
            assertEquals(lastUpdate, rates.get(last - 3).getDate());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
