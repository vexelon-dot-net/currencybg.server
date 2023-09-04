package net.vexelon.currencybg.srv;

import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.api.ApiVerticle;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

	private static ScheduledExecutorService executor = null;

	public static void main(final String[] args) {
		System.out.println("*** CurrencyBG server boot-up ***");

		try {
			// setup a background tasks executor with a minimum of 2 parallel threads
			int parallelThreads = Math.max(2, ForkJoinPool.getCommonPoolParallelism());
			System.out.println("Max threads = " + parallelThreads);
			executor = Executors.newScheduledThreadPool(parallelThreads);

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
	}
}
