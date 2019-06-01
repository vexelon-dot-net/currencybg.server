package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;

public class AllianzSourceTest {

//	@Test
//	public void test_Allianz01() {
//		try {
//			List<CurrencyData> rates = new AllianzSource(new NullReporter())
//			        .getAllianzRates(TestUtils.getTestResource("/allianz_01.html"));
//
//			assertTrue(rates.get(0).getCode().equals("USD"));
//			assertTrue(rates.get(0).getRatio() == 1);
//			assertTrue(rates.get(0).getBuy().equals("1.6676"));
//			assertTrue(rates.get(0).getSell().equals("1.7376"));
//
//			assertTrue(rates.get(3).getCode().equals("GBP"));
//			assertTrue(rates.get(3).getRatio() == 1);
//			assertTrue(rates.get(3).getBuy().equals("2.1542"));
//			assertTrue(rates.get(3).getSell().equals("2.2242"));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//	}

    @Test
    public void test_Allianz_XML() {
        try {
            List<CurrencyData> rates = new AllianzSource(new NullReporter())
                    .getAllianzRates(TestUtils.getTestResource("/allianz_02.xml"));

            assertTrue(rates.get(0).getCode().equals("CHF"));
            assertTrue(rates.get(0).getRatio() == 1);
            assertTrue(rates.get(0).getBuy().equals("1.7192"));
            assertTrue(rates.get(0).getSell().equals("1.7742"));

            assertTrue(rates.get(1).getCode().equals("DKK"));
            assertTrue(rates.get(1).getRatio() == 10);
            assertTrue(rates.get(1).getBuy().equals("2.5895"));
            assertTrue(rates.get(1).getSell().equals("2.6485"));

            assertTrue(rates.get(2).getCode().equals("EUR"));
            assertTrue(rates.get(2).getRatio() == 1);
            assertTrue(rates.get(2).getBuy().equals("1.95"));
            assertTrue(rates.get(2).getSell().equals("1.959"));

            assertTrue(rates.get(4).getCode().equals("JPY"));
            assertTrue(rates.get(4).getRatio() == 100);
            assertTrue(rates.get(4).getBuy().equals("1.5835"));
            assertTrue(rates.get(4).getSell().equals("1.6425"));

            assertTrue(rates.get(7).getCode().equals("USD"));
            assertTrue(rates.get(7).getRatio() == 1);
            assertTrue(rates.get(7).getBuy().equals("1.7329"));
            assertTrue(rates.get(7).getSell().equals("1.7819"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
