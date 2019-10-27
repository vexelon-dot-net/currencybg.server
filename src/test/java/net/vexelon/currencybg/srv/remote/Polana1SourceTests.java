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
    public void test_Polana1_01() {
        try {
            List<CurrencyData> rates = new Polana1(new NullReporter())
                    .getPolana1(TestUtils.getTestResource("/polana1.html"));

            int last = rates.size() - 1;
            Date lastUpdate = TestUtils.newDate(2019, 10, 26,
                    LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA)).getHour(),
                    LocalDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA)).getMinute()); // 02.02.2017

            assertTrue(rates.get(0).getCode().equals("EUR"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("1.95"));
            assertTrue(rates.get(0).getSell().equals("1.958"));
            assertEquals(lastUpdate, rates.get(0).getDate());

            assertTrue(rates.get(last).getCode().equals("BAM"));
            assertTrue(rates.get(last).getRatio() == 1);
            assertTrue(rates.get(last).getBuy().equals("0.99"));
            assertTrue(rates.get(last).getSell().equals("1.03"));
            assertEquals(lastUpdate, rates.get(last).getDate());

            assertTrue(rates.get(last - 1).getCode().equals("ALL"));
            assertTrue(rates.get(last - 1).getRatio() == 100);
            assertTrue(rates.get(last - 1).getBuy().equals("1.63"));
            assertTrue(rates.get(last - 1).getSell().equals("1.73"));
            assertEquals(lastUpdate, rates.get(last - 1).getDate());

            assertTrue(rates.get(last - 6).getCode().equals("RUB"));
            assertTrue(rates.get(last - 6).getRatio() == 100);
            assertTrue(rates.get(last - 6).getBuy().equals("2.68"));
            assertTrue(rates.get(last - 6).getSell().equals("2.79"));
            assertEquals(lastUpdate, rates.get(last - 6).getDate());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
