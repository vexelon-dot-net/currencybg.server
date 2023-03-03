package net.vexelon.currencybg.srv.apix.junctions;

import com.google.common.net.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class Currencies {

	public static final String JUNCTION_BASE = "/api/currencies";

	public void attach(Router router) {
		router.route(JUNCTION_BASE).handler(this::getJunctions);
	}

	public void getJunctions(RoutingContext ctx) {
		String baseUri = StringUtils.removeEnd(ctx.request().absoluteURI(), "/");

		var junctions = Map.of("currencies_from_date_url", baseUri + "/{date}", "currencies_from_date_for_source_url",
				baseUri + "/{date}/{source_id}", "currencies_on_date_url", baseUri + "/today/{time_from}/{source_id}",
				"currencies_on_date_for_source_url", baseUri + "/today/{time_from}/{source_id}");
		ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(Json.encode(junctions));
	}
}
