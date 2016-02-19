package net.vexelon.currencybg.srv.servlets;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.vexelon.currencybg.srv.Bootstrap;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.Heartbeat;

public class MyServletContextListener implements ServletContextListener {

	private static final String THREAD_POOL_ALIAS = "_cbgthpool";
	private static final int MAX_THREADS = 2;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("*** CurrencyBG server start ***");

		/*
		 * Sanity checks and first-time setup
		 */
		new Bootstrap().init();

		/*
		 * Start update thread
		 */
		final ScheduledExecutorService executor = Executors.newScheduledThreadPool(MAX_THREADS);
		sce.getServletContext().setAttribute(THREAD_POOL_ALIAS, executor);
		executor.scheduleWithFixedDelay(new Heartbeat(), Defs.UPDATE_FIRST_INTERVAL, Defs.UPDATES_PERIODIC_INTERVAL,
				TimeUnit.SECONDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("*** CurrencyBG server shutdown ***");

		final ScheduledExecutorService executor = (ScheduledExecutorService) sce.getServletContext()
				.getAttribute(THREAD_POOL_ALIAS);
		executor.shutdown();
	}

}
