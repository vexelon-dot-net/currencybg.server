package net.vexelon.currencybg.srv.apix;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class ApiVerticle extends AbstractVerticle {

	private final Runnable onStopHook;

	public ApiVerticle(Runnable onStopHook) {
		this.onStopHook = onStopHook;
	}

	@Override
	public void start() throws Exception {
		var router = Router.router(vertx);
		router.route("/").handler(new ApiJunctions()::handle);

		int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
		vertx.createHttpServer().requestHandler(router).listen(port);
	}

	@Override
	public void stop() throws Exception {
		onStopHook.run();
	}
}
