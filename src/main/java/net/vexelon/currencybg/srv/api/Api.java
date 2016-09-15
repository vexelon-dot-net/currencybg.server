package net.vexelon.currencybg.srv.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;

@Path("/")
public class Api extends AbstractJunction {

	@Context
	private UriInfo uri;

	@GET
	public Response getApiContents() throws Exception {
		JsonObject junctionsJson = new JsonObject();
		String baseUri = StringUtils.removeEnd(uri.getBaseUri().toString(), "/");

		Currencies.getJunctionsList().entrySet().stream().forEach(e -> {
			junctionsJson.addProperty(e.getKey(), baseUri + e.getValue());
		});

		return getJsonResponse(junctionsJson);
	}

}
