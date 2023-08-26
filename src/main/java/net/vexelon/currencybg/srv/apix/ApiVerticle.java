package net.vexelon.currencybg.srv.apix;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.apix.junctions.Currencies;
import net.vexelon.currencybg.srv.apix.junctions.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiVerticle extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(ApiVerticle.class);

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

		log.info("Starting server on {}:{}", Defs.CONFIG_HOST, Defs.CONFIG_PORT);

		vertx.createHttpServer().requestHandler(router).listen(Integer.parseInt(Defs.CONFIG_PORT), Defs.CONFIG_HOST);
	}

	@Override
	public void stop() throws Exception {
		onStopHook.run();
	}
}
