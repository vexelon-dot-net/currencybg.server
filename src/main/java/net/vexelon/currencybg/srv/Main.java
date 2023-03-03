package net.vexelon.currencybg.srv;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.apix.ApiVerticle;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

	private static final int MAX_THREADS = 3;

	private static ScheduledExecutorService executor = null;

	public static void main(final String[] args) {
		System.out.println("*** CurrencyBG server start ***");

		try {
			executor = Executors.newScheduledThreadPool(MAX_THREADS);

			// init application and boot background services
			var bootstrap = new Bootstrap();
			bootstrap.start(executor);

			// start API server
			var vertx = Vertx.vertx();
			vertx.deployVerticle(new ApiVerticle(Main::onStop));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void onStop() {
		System.out.println("*** CurrencyBG server shutdown ***");

		GlobalConfig.INSTANCE.close();

		// release threads
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

		var drivers = DriverManager.getDrivers();
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
