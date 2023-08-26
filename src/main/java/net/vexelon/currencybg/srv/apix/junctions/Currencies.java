package net.vexelon.currencybg.srv.apix.junctions;

import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Currencies {

	public static final  String JUNCTION_BASE = "/api/currencies";
	private static final Logger log           = LoggerFactory.getLogger(Currencies.class);

	public void attach(Router router) {
		router.route(JUNCTION_BASE).handler(this::getJunctions);
		router.route(JUNCTION_BASE + "/:date").handler(this::fromDate);
	}

	private void getJunctions(RoutingContext ctx) {
		String baseUri = StringUtils.removeEnd(ctx.request().absoluteURI(), "/");

		var junctions = Map.of(
				// ---
				"currencies_from_date_url", baseUri + "/{date}",
				// ---
				"currencies_from_date_for_source_url", baseUri + "/{date}/{source_id}",
				// ---
				"currencies_on_date_url", baseUri + "/today/{time_from}/{source_id}",
				// ---
				"currencies_on_date_for_source_url", baseUri + "/today/{time_from}/{source_id}"
				// ---
		);
		ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(Json.encode(junctions));
	}

	private void fromDate(RoutingContext ctx) {
		// TODO
		//			if (!source.isCheckAuthentication(apiKey)) {
		//				throw new ApiAccessException(Response.Status.UNAUTHORIZED);
		//			}
		//		return getCustomResponse(e.getStatus());

		ctx.vertx().executeBlocking((Promise<String> promise) -> {
			try (final var source = DataSource.newDataSource()) {
				source.connect();
				promise.complete(source.getAllRates(DateTimeUtils.parseDate(ctx.pathParam("date"), Defs.DATE_FORMAT)));
			} catch (Exception e) {
				promise.fail(e);
			}
		}).onSuccess(json -> {
			ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
			ctx.response().end(json);
		}).onFailure(t -> {
			log.error("Failed fetching all rates from specified date!", t);
			ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
		});
	}
}
