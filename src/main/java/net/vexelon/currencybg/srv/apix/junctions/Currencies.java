package net.vexelon.currencybg.srv.apix.junctions;

import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.ApiAccessException;
import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
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

	private void sendError(RoutingContext ctx, Throwable t) {
		if (t instanceof ApiAccessException ex) {
			ctx.response().setStatusCode(ex.getStatus().getStatusCode());
			ctx.response().end();
			log.debug("Unauthorized request from {}", ctx.request().remoteAddress().hostAddress());
		} else {
			ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
			ctx.response().end();
			log.error("Failed fetching all rates from specified date!", t);
		}
	}

	private void sendJson(RoutingContext ctx, String jsonString) {
		ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(jsonString);
	}

	private void fromDate(RoutingContext ctx) {
		ctx.vertx().executeBlocking((Promise<String> promise) -> {
			try (final var source = DataSource.newDataSource()) {
				source.connect();

				if (!source.isCheckAuthentication(ctx.request().getHeader(Defs.HEADER_APIKEY))) {
					throw new ApiAccessException(Response.Status.UNAUTHORIZED);
				}

				promise.complete(source.getAllRates(DateTimeUtils.parseDate(ctx.pathParam("date"), Defs.DATE_FORMAT)));
			} catch (Exception e) {
				promise.fail(e);
			}
		}).onSuccess(json -> sendJson(ctx, json)).onFailure(t -> sendError(ctx, t));
	}
}
