
import java.util.Date;

import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.HibernateDataSource;

public class App {
	public static void main(String[] args) {

		HibernateDataSource source = new HibernateDataSource();
		try {
			source.getAllCurrentRatesAfter(new Date());
		} catch (DataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Session session = HibernateUtil.getSessionFactory().openSession();
		// session.beginTransaction();
		//
		// APIKey apiKey = new APIKey();
		// apiKey.setKeyValue("Ivan");
		// apiKey.setStatus(7);
		//
		// CurrencyData currData = new CurrencyData();
		// currData.setCode("YYY");
		// currData.setBuy("2.50");
		// currData.setSell("3.66");
		// currData.setRatio(5);
		// currData.setSource(9);
		// currData.setDate(new Date());
		//
		// Report report = new Report();
		// report.setCreatedOn(new Date());
		// report.setMessage("ooooooooo");
		//
		// session.save(apiKey);
		// session.save(currData);
		// session.save(report);
		//
		// session.getTransaction().commit();
		//
		// HibernateUtil.shutdown();
		// System.out.println("Great! APIKey was saved");
	}
}