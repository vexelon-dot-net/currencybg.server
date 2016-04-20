package net.vexelon.currencybg.srv.api;

import java.util.Map;

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

		Map<String, String> currenciesJunctionsMap = Currencies.getJunctionsList();
		for (java.util.Map.Entry<String, String> entry : currenciesJunctionsMap.entrySet()) {
			junctionsJson.addProperty(baseUri + entry.getKey(), entry.getValue());
		}

		return getJsonResponse(junctionsJson);
	}

}
