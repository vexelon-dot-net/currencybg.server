package net.vexelon.currencybg.srv.apix.junctions;

import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.AbstractJunction;
import net.vexelon.currencybg.srv.api.ApiAccessException;
import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import java.util.Map;

public class Currencies extends AbstractJunction {

	public static final String JUNCTION_BASE = "/api/currencies";

	public void attach(Router router) {
		router.route(JUNCTION_BASE).handler(this::getJunctions);
		router.route(JUNCTION_BASE + "/:date").handler(this::fromDate);
		router.route(JUNCTION_BASE + "/:date/:source_id").handler(this::fromDateAndSource);
	}

	private void getJunctions(RoutingContext ctx) {
		String baseUri = StringUtils.removeEnd(ctx.request().absoluteURI(), "/");
		sendJson(ctx, Json.encode(Map.of(
				// ---
				"currencies_from_date_url", baseUri + "/{date}",
				// ---
				"currencies_from_date_for_source_url", baseUri + "/{date}/{source_id}",
				// ---
				"currencies_on_date_url", baseUri + "/today/{time_from}/{source_id}",
				// ---
				"currencies_on_date_for_source_url", baseUri + "/today/{time_from}/{source_id}"
				// ---
		)));
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

	private void fromDateAndSource(RoutingContext ctx) {
		ctx.vertx().executeBlocking((Promise<String> promise) -> {
			try (final var source = DataSource.newDataSource()) {
				source.connect();

				if (!source.isCheckAuthentication(ctx.request().getHeader(Defs.HEADER_APIKEY))) {
					throw new ApiAccessException(Response.Status.UNAUTHORIZED);
				}

				promise.complete(source.getAllRates(Integer.parseInt(ctx.pathParam("source_id")),
						DateTimeUtils.parseDate(ctx.pathParam("date"), Defs.DATE_FORMAT)));
			} catch (Exception e) {
				promise.fail(e);
			}
		}).onSuccess(json -> sendJson(ctx, json)).onFailure(t -> sendError(ctx, t));
	}
}
