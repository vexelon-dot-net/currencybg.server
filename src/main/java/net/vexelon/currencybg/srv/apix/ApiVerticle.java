package net.vexelon.currencybg.srv.apix;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.apix.junctions.Currencies;
import net.vexelon.currencybg.srv.apix.junctions.Root;

public class ApiVerticle extends AbstractVerticle {

	private final Runnable onStopHook;

	public ApiVerticle(Runnable onStopHook) {
		this.onStopHook = onStopHook;
	}

	@Override
	public void start() throws Exception {
		var router = Router.router(vertx);

		// the attach order matters!
		new Currencies().attach(router);
		new Root().attach(router);

		vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(Defs.CONFIG_PORT));
	}

	@Override
	public void stop() throws Exception {
		onStopHook.run();
	}
}
