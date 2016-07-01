package net.vexelon.currencybg.srv.servlets;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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

		final ScheduledExecutorService executor = (ScheduledExecutorService) sce.getServletContext()
				.getAttribute(THREAD_POOL_ALIAS);
		if (executor != null) {
			executor.shutdown();
		}
	}

}
