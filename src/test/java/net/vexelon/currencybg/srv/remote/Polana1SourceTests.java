package net.vexelon.currencybg.srv.remote;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

import static org.junit.Assert.*;

public class Polana1SourceTests {

    @Test
    public void test_polana1_1() {
        try {
            List<CurrencyData> rates = new Polana1(new NullReporter())
                    .getPolana1(TestUtils.getTestResource("/polana1.html"));

            int last = rates.size() - 1;
            Date lastUpdate = TestUtils.newDate(2020, 2, 8,
                    LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA)).getHour(),
                    LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA)).getMinute());

            assertEquals("EUR", rates.get(0).getCode());
            assertEquals(1, rates.get(0).getRatio());
            assertEquals("1.953", rates.get(0).getBuy());
            assertEquals("1.969", rates.get(0).getSell());
            assertEquals(lastUpdate, rates.get(0).getDate());

            assertEquals("BAM", rates.get(last).getCode());
            assertEquals(1, rates.get(last).getRatio());
            assertEquals("0.98", rates.get(last).getBuy());
            assertEquals("1.03", rates.get(last).getSell());
            assertEquals(lastUpdate, rates.get(last).getDate());

            assertEquals("ALL", rates.get(last - 1).getCode());
            assertEquals(100, rates.get(last - 1).getRatio());
            assertEquals("1.56", rates.get(last - 1).getBuy());
            assertEquals("1.68", rates.get(last - 1).getSell());
            assertEquals(lastUpdate, rates.get(last - 1).getDate());

            assertEquals("RUB", rates.get(last - 6).getCode());
            assertEquals(100, rates.get(last - 6).getRatio());
            assertEquals("2.75", rates.get(last - 6).getBuy());
            assertEquals("2.89", rates.get(last - 6).getSell());
            assertEquals(lastUpdate, rates.get(last - 6).getDate());

            assertEquals("ILS", rates.get(last - 3).getCode());
            assertEquals(1, rates.get(last - 3).getRatio());
            assertEquals("", rates.get(last - 3).getBuy());
            assertEquals("0.53", rates.get(last - 3).getSell());
            assertEquals(lastUpdate, rates.get(last - 3).getDate());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
