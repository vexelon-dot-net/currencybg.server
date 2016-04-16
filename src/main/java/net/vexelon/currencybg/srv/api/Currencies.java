package net.vexelon.currencybg.srv.api;

import java.util.Date;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.DataSourceInterface;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

@Path(Currencies.JUNCTION_NAME)
public class Currencies {

	private static final Logger log = LoggerFactory.getLogger(Currencies.class);

	public static final String JUNCTION_NAME = "/currencies";

	/**
	 * Fetches a key/value set of all available junctions.
	 * 
	 * @return
	 */
	public static Map<String, String> getJunctionsList() {
		Map<String, String> junctionsMap = Maps.newHashMap();
		junctionsMap.put("currencies_fromdate_url", JUNCTION_NAME + "/{date}");
		junctionsMap.put("currencies_fixed_fromdate_url", JUNCTION_NAME + "/fixed/{date}");
		junctionsMap.put("currencies_nonfixed_fromdate_url", JUNCTION_NAME + "/nonfixed/{date}");
		return junctionsMap;
	}

	@GET
	@Path("/{dateFrom}")
	public Response getAllRatesByDate(@PathParam("dateFrom") String initialDate, @HeaderParam("APIKey") String APIValue)
			throws Exception {

		Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);
		log.debug("test header {}", APIValue);

		String currencies = null;
		DataSourceInterface source = null;

		try {

			source = new DataSource();
			source.dbConnect();
			if (!source.checkAuthentication(APIValue)) {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			currencies = source.getAllRatesByDate(dateFrom);
		} catch (DataSourceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			IOUtils.closeQuietly(source);
		}
		return Response.status(Status.OK).entity(currencies)
				.header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE).build();

	}

	@GET
	@Path("/nonfixed/{dateFrom}")
	public Response getNonFixedRates(@PathParam("dateFrom") String initialDate, @HeaderParam("APIKey") String APIValue)
			throws Exception {

		Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);

		String currencies = null;
		DataSourceInterface source = null;
		try {
			source = new DataSource();
			source.dbConnect();
			if (!source.checkAuthentication(APIValue)) {
				return Response.status(Response.Status.UNAUTHORIZED).entity("ERROR").build();
			}
			currencies = source.getNonFixedRates(dateFrom);
		} catch (DataSourceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} finally {
			IOUtils.closeQuietly(source);
		}

		return Response.status(Status.OK).entity(currencies)
				.header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE).build();

	}

	@GET
	@Path("/fixed/{dateFrom}")
	public Response getFixedRates(@PathParam("dateFrom") String initialDate, @HeaderParam("APIKey") String APIValue)
			throws Exception {

		Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);

		String currencies = null;
		DataSourceInterface source = null;
		try {
			source = new DataSource();
			source.dbConnect();
			if (!source.checkAuthentication(APIValue)) {
				return Response.status(Response.Status.UNAUTHORIZED).entity("ERROR").build();
			}
			currencies = source.getFixedRates(dateFrom);
		} catch (DataSourceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} finally {
			IOUtils.closeQuietly(source);
		}

		return Response.status(Status.OK).entity(currencies)
				.header(HttpHeaders.CONTENT_TYPE, Defs.API_JSON_CONTENT_TYPE).build();

	}

}
