package net.vexelon.currencybg.srv.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.JsonObject;

@Path("/")
public class Api extends AbstractJunction {

	@Context
	private UriInfo uri;

	@GET
	public Response getApiContents() throws Exception {
		JsonObject junctionsJson = new JsonObject();
		String baseUri = uri.getBaseUri().toString();

		Currencies.getJunctionsList().entrySet().stream().forEach(e -> {
			junctionsJson.addProperty(baseUri + e.getKey(), e.getValue());
		});

		return getJsonResponse(junctionsJson);
	}

}
