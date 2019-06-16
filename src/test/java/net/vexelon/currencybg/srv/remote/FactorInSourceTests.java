package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class FactorInSourceTests {

    @Test
    public void test_FactorIn01() {
        try {
            List<CurrencyData> rates = new Factorin(new NullReporter())
                    .getFactorinRates(TestUtils.getTestResource("/factorin_06.2019.html"));

            int last = rates.size() - 1;
            Date lastUpdate = TestUtils.newDate(2019, 6, 15, 13, 9);

            assertTrue(rates.get(0).getCode().equals("EUR"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("1.953"));
            assertTrue(rates.get(0).getSell().equals("1.959"));
            assertTrue(rates.get(0).getDate().equals(lastUpdate));

            assertTrue(rates.get(6).getCode().equals("TRY"));
            assertTrue(rates.get(6).getRatio() == 1);
            assertTrue(rates.get(6).getBuy().equals("0.3"));
            assertTrue(rates.get(6).getSell().equals("0.309"));
            assertTrue(rates.get(6).getDate().equals(lastUpdate));

            assertTrue(rates.get(last).getCode().equals("PHP"));
            assertTrue(rates.get(last).getRatio() == 1);
            assertTrue(rates.get(last).getBuy().equals("0.034"));
            assertTrue(rates.get(last).getSell().equals("0.035"));
            assertTrue(rates.get(last).getDate().equals(lastUpdate));

            assertTrue(rates.get(last - 10).getCode().equals("TWD"));
            assertTrue(rates.get(last - 10).getRatio() == 1);
            assertTrue(rates.get(last - 10).getBuy().equals("0.052"));
            assertTrue(rates.get(last - 10).getSell().equals("0.057"));
            assertTrue(rates.get(last - 10).getDate().equals(lastUpdate));

            assertTrue(rates.get(last - 16).getCode().equals("KRW"));
            assertTrue(rates.get(last - 16).getRatio() == 1);
            assertTrue(rates.get(last - 16).getBuy().equals("0.00146"));
            assertTrue(rates.get(last - 16).getSell().equals("0.00152"));
            assertTrue(rates.get(last - 16).getDate().equals(lastUpdate));

            assertTrue(rates.get(last - 24).getCode().equals("BAM"));
            assertTrue(rates.get(last - 24).getRatio() == 1);
            assertTrue(rates.get(last - 24).getBuy().equals("1"));
            assertTrue(rates.get(last - 24).getSell().equals("1.04"));
            assertTrue(rates.get(last - 24).getDate().equals(lastUpdate));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}