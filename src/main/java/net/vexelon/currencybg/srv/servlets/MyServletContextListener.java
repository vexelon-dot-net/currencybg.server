package net.vexelon.currencybg.srv.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.vexelon.currencybg.srv.Bootstrap;

public class MyServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("*** CurrencyBG server start ***");
		new Bootstrap().init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("*** CurrencyBG server shutdown ***");
	}

}
