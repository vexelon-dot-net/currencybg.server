package net.vexelon.currencybg.srv.servlets;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import net.vexelon.currencybg.srv.Bootstrap;

public class MyServletContextListener implements ServletContextListener {

	private static final String THREAD_POOL_ALIAS = "_cbgthpool";
	private static final int MAX_THREADS = 3;

	private Bootstrap bootstrap = new Bootstrap();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("*** CurrencyBG server start ***");

		final ScheduledExecutorService executor = Executors.newScheduledThreadPool(MAX_THREADS);
		sce.getServletContext().setAttribute(THREAD_POOL_ALIAS, executor);

		/*
		 * Sanity checks and first-time setup
		 */
		bootstrap.start(executor);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("*** CurrencyBG server shutdown ***");

		bootstrap.stop();

		// release threads
		final ScheduledExecutorService executor = (ScheduledExecutorService) sce.getServletContext()
		        .getAttribute(THREAD_POOL_ALIAS);
		if (executor != null) {
			System.out.println("*** Shutting down threads ***");
			executor.shutdown();
		}

		// release database driver(s)
		// @see http://stackoverflow.com/a/16467695
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (Throwable e) {
			// do nothing
		}

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (Exception e) {
				System.err.println("*** Failed to unregister sql driver -" + driver.toString() + "! ***");
			}
		}
	}

}
