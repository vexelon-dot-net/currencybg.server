package net.vexelon.currencybg.srv.remote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import net.vexelon.currencybg.srv.db.models.CurrencyData;

public class BNBSourceTests {

	private InputStream getTestResource(String path) {
		return getClass().getResourceAsStream(path);
	}

	private Date newDate(int y, int m, int d) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d);
		System.out.println(cal.getTime());
		return cal.getTime();
	}

	@Test
	public void testEN() {
		try {
			List<CurrencyData> rates = new BNBSource().getBNBRates(getTestResource("/exchange_rates_en.xml"));
			assertTrue(rates.get(0).getCode().equals("AUD"));
			assertTrue(rates.get(0).getRatio() == 1);
			// assertTrue(rates.get(0).getReverseRate().equals("0.760647"));
			// assertTrue(rates.get(0).getRate().equals("1.31467"));
			assertTrue(rates.get(0).getDate().equals(newDate(2016, 3, 15))); // 15.03.2016

			assertTrue(rates.get(3).getCode().equals("CHF"));
			assertTrue(rates.get(3).getRatio() == 1);
			// assertTrue(rates.get(3).getReverseRate().equals("0.56007"));
			// assertTrue(rates.get(3).getRate().equals("1.78549"));
			assertTrue(rates.get(3).getDate().equals(newDate(2016, 3, 15))); // 15.03.2016

			assertTrue(rates.get(14).getCode().equals("ISK"));
			assertTrue(rates.get(14).getRatio() == 0);
			// assertTrue(rates.get(14).getReverseRate().equals("0"));
			// assertTrue(rates.get(14).getRate().equals("0"));
			assertTrue(rates.get(14).getDate().equals(newDate(2016, 3, 15))); // 15.03.2016

			int pos = rates.size() - 1;
			assertTrue(rates.get(pos).getCode().equals("XAU"));
			assertTrue(rates.get(pos).getRatio() == 1);
			// assertTrue(rates.get(pos).getReverseRate().equals("0"));
			// assertTrue(rates.get(pos).getRate().equals("2175.97"));
			assertTrue(rates.get(pos).getDate().equals(newDate(2016, 3, 15))); // 15.03.2016

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	// TODO - Вече теглим само единия файл. да се решо да има нужда от 2
	// теставввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввввсс
	/*
	 * @Test
	 * public void testBG() {
	 * try {
	 * List<CurrencyData> rates = new
	 * BNBSource().getBNBRates(getTestResource("/exchange_rates_bg.xml"));
	 * assertTrue(rates.get(0).getName().equals("Австралийски долар"));
	 * assertTrue(rates.get(0).getGold() == 1);
	 * assertTrue(rates.get(0).getCode().equals("AUD"));
	 * assertTrue(rates.get(0).getRatio() == 1);
	 * System.out.println(rates.get(0).getReverseRate());
	 * assertTrue(rates.get(0).getReverseRate().equals("0.754973"));
	 * assertTrue(rates.get(0).getRate().equals("1.32455"));
	 * assertTrue(rates.get(0).getCurrDate().equals(newDate(2016, 3, 11))); //
	 * 11.03.2016
	 * assertTrue(rates.get(0).getfStar() == 0);
	 * 
	 * assertTrue(rates.get(3).getName().equals("Швейцарски франк"));
	 * assertTrue(rates.get(3).getGold() == 1);
	 * assertTrue(rates.get(3).getCode().equals("CHF"));
	 * assertTrue(rates.get(3).getRatio() == 1);
	 * assertTrue(rates.get(3).getReverseRate().equals("0.559763"));
	 * assertTrue(rates.get(3).getRate().equals("1.78647"));
	 * assertTrue(rates.get(3).getCurrDate().equals(newDate(2016, 3, 11))); //
	 * 11.03.2016
	 * assertTrue(rates.get(3).getfStar() == 0);
	 * 
	 * assertTrue(rates.get(14).getName().equals("Исландска крона *"));
	 * assertTrue(rates.get(14).getGold() == 1);
	 * assertTrue(rates.get(14).getCode().equals("ISK"));
	 * assertTrue(rates.get(14).getRatio() == 0);
	 * assertTrue(rates.get(14).getReverseRate().equals("0"));
	 * assertTrue(rates.get(14).getRate().equals("0"));
	 * assertTrue(rates.get(14).getCurrDate().equals(newDate(2016, 3, 11))); //
	 * 11.03.2016
	 * assertTrue(rates.get(14).getfStar() == 1);
	 * 
	 * int pos = rates.size() - 1;
	 * assertTrue(rates.get(pos).getName().equals("Злато (1 трой унция)"));
	 * assertTrue(rates.get(pos).getGold() == 2);
	 * assertTrue(rates.get(pos).getCode().equals("XAU"));
	 * assertTrue(rates.get(pos).getRatio() == 1);
	 * assertTrue(rates.get(pos).getReverseRate().equals("0"));
	 * assertTrue(rates.get(pos).getRate().equals("2222.79"));
	 * assertTrue(rates.get(pos).getCurrDate().equals(newDate(2016, 3, 11))); //
	 * 11.03.2016
	 * assertTrue(rates.get(pos).getfStar() == 0);
	 * 
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * fail(e.getMessage());
	 * }
	 * }
	 */

}
