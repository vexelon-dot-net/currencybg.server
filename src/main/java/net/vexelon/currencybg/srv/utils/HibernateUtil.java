package net.vexelon.currencybg.srv.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	@SuppressWarnings("deprecation")
	private static SessionFactory buildSessionFactory() {
		try {
			// Use hibernate.cfg.xml to get a SessionFactory
			/** Load the hibernate.cfg.xml from the classpath **/
			Configuration cfg = new Configuration();
			cfg.setProperty("hibernate.connection.password", System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD"));
			return cfg.configure().buildSessionFactory();
			// return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}

}