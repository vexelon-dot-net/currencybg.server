package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class TavexSourceTests {

//    @Test
//    public void test_Tavex02() {
//        try {
//            List<CurrencyData> rates = new TavexSource(new NullReporter())
//                    .getTavexRates(TestUtils.getTestResource("/tavex02.html"));
//
//            int last = rates.size() - 1;
//
//            assertTrue(rates.get(0).getCode().equals("EUR"));
//            assertTrue(rates.get(0).getRatio() == 1);
//            assertTrue(rates.get(0).getBuy().equals("1.950"));
//            assertTrue(rates.get(0).getSell().equals("1.958"));
//            // assertTrue(rates.get(0).getDate().equals(lastUpdate));
//
//            assertTrue(rates.get(last).getCode().equals("AZN"));
//            assertTrue(rates.get(last).getRatio() == 1);
//            assertTrue(rates.get(last).getBuy().equals("0.984"));
//            assertTrue(rates.get(last).getSell().equals("1.101"));
//            // assertTrue(rates.get(last).getDate().equals(lastUpdate));
//
//            assertTrue(rates.get(last - 3).getCode().equals("VND"));
//            assertTrue(rates.get(last - 3).getRatio() == 1);
//            assertTrue(rates.get(last - 3).getBuy().equals("0.0000690"));
//            assertTrue(rates.get(last - 3).getSell().equals("0.0000860"));
//            // assertTrue(rates.get(last - 18).getDate().equals(lastUpdate));
//
//            assertTrue(rates.get(24).getCode().equals("EEK"));
//            assertTrue(rates.get(24).getRatio() == 1);
//            assertTrue(rates.get(24).getBuy().equals("0.119"));
//            assertTrue(rates.get(24).getSell().isEmpty());
//            // assertTrue(rates.get(last - 19).getDate().equals(lastUpdate));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//    }

    @Test
    public void test_Tavex03() {
        try {
            List<CurrencyData> rates = new TavexSource(new NullReporter())
                    .getTavexRates(TestUtils.getTestResource("/tavex03.html"));

            int last = rates.size() - 1;

            assertTrue(rates.get(0).getCode().equals("EUR"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("1.9500"));
            assertTrue(rates.get(0).getSell().equals("1.9560"));
            // assertTrue(rates.get(0).getDate().equals(lastUpdate));

            assertTrue(rates.get(last).getCode().equals("AZN"));
            assertTrue(rates.get(last).getRatio() == 1);
            assertTrue(rates.get(last).getBuy().equals("0.957"));
            assertTrue(rates.get(last).getSell().equals("1.158"));
            // assertTrue(rates.get(last).getDate().equals(lastUpdate));

            assertTrue(rates.get(last - 3).getCode().equals("VND"));
            assertTrue(rates.get(last - 3).getRatio() == 1);
            assertTrue(rates.get(last - 3).getBuy().equals("0.0000690"));
            assertTrue(rates.get(last - 3).getSell().equals("0.0000880"));
            // assertTrue(rates.get(last - 18).getDate().equals(lastUpdate));

            assertTrue(rates.get(24).getCode().equals("EEK"));
            assertTrue(rates.get(24).getRatio() == 1);
            assertTrue(rates.get(24).getBuy().equals("0.118"));
            assertTrue(rates.get(24).getSell().isEmpty());
            // assertTrue(rates.get(last - 19).getDate().equals(lastUpdate));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
