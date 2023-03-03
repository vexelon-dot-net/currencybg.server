package net.vexelon.currencybg.srv.apix;

import com.google.common.net.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.AbstractJunction;
import net.vexelon.currencybg.srv.api.Currencies;
import org.apache.commons.lang3.StringUtils;

public class ApiJunctions extends AbstractJunction {

	public void handle(RoutingContext ctx) {
		var junctionsJson = new JsonObject();
		String baseUri = StringUtils.removeEnd(ctx.request().uri(), "/");

		Currencies.getJunctionsList().entrySet().forEach(e -> {
			junctionsJson.put(e.getKey(), baseUri + e.getValue());
		});

		ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(junctionsJson.encode());
	}
}
