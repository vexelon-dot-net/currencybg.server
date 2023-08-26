package net.vexelon.currencybg.srv.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.vexelon.currencybg.srv.Defs;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Wraps common functionalities for all API junctions
 */
public abstract class AbstractJunction {

	protected Response getJsonResponse(String entity) {
		return Response.status(Status.OK).entity(entity).header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE)
				.build();
	}

	protected Response getJsonResponse(JsonObject jsonEntity) {
		return Response.status(Status.OK).entity(new Gson().toJson(jsonEntity))
				.header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE).build();
	}

	protected Response getCustomResponse(Response.Status status) {
		return Response.status(status).build();
	}

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
}
