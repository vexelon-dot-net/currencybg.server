package net.vexelon.currencybg.srv.api;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.DataSourceInterface;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

@Path(Currencies.JUNCTION_NAME)
public class Currencies extends AbstractJunction {

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
	public Response getAllRatesByDate(@PathParam("dateFrom") String initialDate,
			@HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSourceInterface source = new DataSource()) {
			source.connect();
			if (!source.isCheckAuthentication(apiKey)) {
				return getCustomResponse(Response.Status.UNAUTHORIZED);
			}

			Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);
			return getJsonResponse(source.getAllRatesByDate(dateFrom));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		}
	}

	@GET
	@Path("/nonfixed/{dateFrom}")
	public Response getNonFixedRates(@PathParam("dateFrom") String initialDate,
			@HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSourceInterface source = new DataSource()) {
			source.connect();

			if (!source.isCheckAuthentication(apiKey)) {
				return getCustomResponse(Response.Status.UNAUTHORIZED);
			}

			Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);
			return getJsonResponse(source.getNonFixedRates(dateFrom));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		}
	}

	@GET
	@Path("/fixed/{dateFrom}")
	public Response getFixedRates(@PathParam("dateFrom") String initialDate,
			@HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSourceInterface source = new DataSource()) {
			source.connect();

			if (!source.isCheckAuthentication(apiKey)) {
				return getCustomResponse(Response.Status.UNAUTHORIZED);
			}

			Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);
			return getJsonResponse(source.getFixedRates(dateFrom));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		}
	}

}
