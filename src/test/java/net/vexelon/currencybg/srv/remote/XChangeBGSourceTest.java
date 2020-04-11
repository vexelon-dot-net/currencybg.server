package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.reports.NullReporter;
import net.vexelon.currencybg.srv.tests.TestUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class XChangeBGSourceTest {
    @Test
    public void test_xchangebg_1() {
        List<CurrencyData> rates;
        CurrencyData first;

        try {
            rates = new XChangeBGSource(new NullReporter())
                    .getXChangeRates(TestUtils.getTestResource("/xbg_btc_buy.json"),
                            TestUtils.getTestResource("/xbg_btc_sell.json"), "BTC-BGN");

            assertEquals("BTC size=1", 1, rates.size());
            first = rates.iterator().next();
            assertEquals("BTC", first.getCode());
            assertEquals(1, first.getRatio());
            assertEquals("12722.80", first.getBuy());
            assertEquals("11974.84", first.getSell());

            rates = new XChangeBGSource(new NullReporter())
                    .getXChangeRates(TestUtils.getTestResource("/xbg_eth_buy.json"),
                            TestUtils.getTestResource("/xbg_eth_sell.json"), "ETH-BGN");

            assertEquals("ETH size=1", 1, rates.size());
            first = rates.iterator().next();
            assertEquals("ETH", first.getCode());
            assertEquals(1, first.getRatio());
            assertEquals("292.98", first.getBuy());
            assertEquals("276.30", first.getSell());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
