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
		junctionsMap.put("currencies_from_date_url", JUNCTION_NAME + "/{date}");
		junctionsMap.put("currencies_from_date&source_url", JUNCTION_NAME + "/{date}/{sourceid}");
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
			return getJsonResponse(source.getAllRates(dateFrom));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		}
	}

	@GET
	@Path("/{dateFrom}/{sourceId}")
	public Response getAllRates(@PathParam("dateFrom") String initialDate, @PathParam("sourceId") Integer sourceId,
			@HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSourceInterface source = new DataSource()) {
			source.connect();
			if (!source.isCheckAuthentication(apiKey)) {
				return getCustomResponse(Response.Status.UNAUTHORIZED);
			}

			Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, Defs.DATETIME_FORMAT);
			return getJsonResponse(source.getAllRates(sourceId, dateFrom));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		}
	}
}
