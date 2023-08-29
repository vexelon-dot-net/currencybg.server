package net.vexelon.currencybg.srv.api.junctions;

import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class Root extends AbstractJunction {

	private static final String JUNCTION_BASE = "/api";

	public void attach(Router router) {
		router.route(JUNCTION_BASE).handler(this::getApiJunctions);
		router.route().handler(ctx -> {
			ctx.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code());
			ctx.response().end();
		});
	}

	private void getApiJunctions(RoutingContext ctx) {
		String baseUri = StringUtils.removeEnd(ctx.request().absoluteURI(), "/");
		ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(Json.encode(Map.of("currencies_url", baseUri + Currencies.JUNCTION_BASE)));
	}
}
