package net.vexelon.currencybg.srv.api.junctions;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.api.ApiAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps common functionalities for all API junctions
 */
public abstract class AbstractJunction {

	private static final Logger log = LoggerFactory.getLogger(AbstractJunction.class);

	protected void sendError(RoutingContext ctx, Throwable t) {
		if (t instanceof ApiAccessException ex) {
			ctx.response().setStatusCode(ex.getStatus().code());
			ctx.response().end(ex.getStatus().reasonPhrase());
			log.debug("Unauthorized request from {}", ctx.request().remoteAddress().hostAddress());
		} else if (t instanceof NumberFormatException) {
			ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
			ctx.response().end(HttpResponseStatus.BAD_REQUEST.reasonPhrase());
			log.error("Failed reading input parameter!", t);
		} else {
			ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
			ctx.response().end(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase());
			log.error("Unexpected API error!", t);
		}
	}

	protected void sendJson(RoutingContext ctx, String jsonString) {
		ctx.response().putHeader(com.google.common.net.HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE);
		ctx.response().end(jsonString);
	}
}
