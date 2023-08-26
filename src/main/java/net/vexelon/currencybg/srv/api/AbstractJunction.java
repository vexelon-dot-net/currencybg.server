package net.vexelon.currencybg.srv.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Wraps common functionalities for all API junctions
 */
public abstract class AbstractJunction {

	private static final Logger log = LoggerFactory.getLogger(AbstractJunction.class);

	@Deprecated
	protected Response getJsonResponse(String entity) {
		return Response.status(Status.OK).entity(entity).header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE)
				.build();
	}

	@Deprecated
	protected Response getJsonResponse(JsonObject jsonEntity) {
		return Response.status(Status.OK).entity(new Gson().toJson(jsonEntity))
				.header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE).build();
	}

	@Deprecated
	protected Response getCustomResponse(Response.Status status) {
		return Response.status(status).build();
	}

	@Deprecated
	protected Response getErrorResponse() {
		return getCustomResponse(Response.Status.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Verifies API is accessible
	 *
	 * @throws ApiAccessException
	 */
	protected void verifyAccess() throws ApiAccessException {
		//		if (GlobalConfig.INSTANCE.isMaintenanceEnabled()) {
		//			throw new ApiAccessException(Status.SERVICE_UNAVAILABLE, "Server maintenance enabled!");
		//		}
	}

	protected void sendError(RoutingContext ctx, Throwable t) {
		if (t instanceof ApiAccessException ex) {
			ctx.response().setStatusCode(ex.getStatus().getStatusCode());
			ctx.response().end();
			log.debug("Unauthorized request from {}", ctx.request().remoteAddress().hostAddress());
		} else if (t instanceof NumberFormatException) {
			ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
			ctx.response().end();
			log.error("Failed reading input parameter!", t);
		} else {
			ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
			ctx.response().end();
			log.error("Unexpected API error!", t);
		}
	}

	protected void sendJson(RoutingContext ctx, String jsonString) {
		ctx.response().putHeader(com.google.common.net.HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(jsonString);
	}
}
