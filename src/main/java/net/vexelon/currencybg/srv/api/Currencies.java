package net.vexelon.currencybg.srv.api;

import java.io.IOException;
import java.text.ParseException;
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
import net.vexelon.currencybg.srv.db.HibernateDataSource;
import net.vexelon.currencybg.srv.db.MySQLDataSource;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import net.vexelon.currencybg.srv.utils.DateTimeUtilsNew;

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
		junctionsMap.put("currencies_from_date_for_source_url", JUNCTION_NAME + "/{date}/{source_id}");
		junctionsMap.put("currencies_on_date_url", JUNCTION_NAME + "/today/{time_from}/{source_id}");
		junctionsMap.put("currencies_on_date_for_source_url", JUNCTION_NAME + "/today/{time_from}/{source_id}");
		return junctionsMap;
	}

	@GET
	@Path("/{dateFrom}")
	public Response getAllRatesByDate(@PathParam("dateFrom") String dateFrom,
	        @HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSource source = new MySQLDataSource()) {
			verifyAccess();

			source.connect();
			if (!source.isCheckAuthentication(apiKey)) {
				throw new ApiAccessException(Response.Status.UNAUTHORIZED);
			}

			return getJsonResponse(source.getAllRates(DateTimeUtils.parseDate(dateFrom, Defs.DATE_FORMAT)));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		} catch (ApiAccessException e) {
			log.debug(e.getMessage());
			return getCustomResponse(e.getStatus());
		}
	}

	@GET
	@Path("/{dateFrom}/{sourceId}")
	public Response getAllRates(@PathParam("dateFrom") String dateFrom, @PathParam("sourceId") Integer sourceId,
	        @HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSource source = new MySQLDataSource()) {
			verifyAccess();

			source.connect();
			if (!source.isCheckAuthentication(apiKey)) {
				throw new ApiAccessException(Response.Status.UNAUTHORIZED);
			}

			return getJsonResponse(source.getAllRates(sourceId, DateTimeUtils.parseDate(dateFrom, Defs.DATE_FORMAT)));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		} catch (ApiAccessException e) {
			log.debug(e.getMessage());
			return getCustomResponse(e.getStatus());
		}

	}

	@GET
	@Path("/today/{timeFrom}/{sourceId}")
	public Response getAllCurrentRatesAfter(@PathParam("timeFrom") String timeFrom,
	        @PathParam("sourceId") Integer sourceId, @HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSource source = /* new MySQLDataSource() */new HibernateDataSource()) {
			verifyAccess();

			source.connect();

			// if (!source.isCheckAuthentication(apiKey)) {
			// throw new ApiAccessException(Response.Status.UNAUTHORIZED);
			// }

			// String localTimeFrom = DateTimeUtils.toTimeZone(timeFrom,
			// Defs.DATETIME_TIMEZONE_SOFIA);
			// String localTimeFromNoTz =
			// DateTimeUtils.removeTimeZone(localTimeFrom,
			// GlobalConfig.INSTANCE.getServerTimeZone());

			String localTimeFromNoTz = DateTimeUtilsNew.removeTimeZone(timeFrom, Defs.DATETIME_FORMAT);

			return getJsonResponse(source.getAllCurrentRatesAfter(sourceId,
			        DateTimeUtils.parseDate(localTimeFromNoTz, Defs.DATETIME_FORMAT)));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		} catch (ApiAccessException e) {
			log.debug(e.getMessage());
			return getCustomResponse(e.getStatus());
		}
	}

	@GET
	@Path("/today/{timeFrom}")
	public Response getAllCurrentRatesAfter(@PathParam("timeFrom") String timeFrom,
	        @HeaderParam(Defs.HEADER_APIKEY) String apiKey) throws Exception {

		try (DataSource source = new HibernateDataSource()
		/* new MySQLDataSource() */) {
			verifyAccess();

			source.connect();
			// if (!source.isCheckAuthentication(apiKey)) {
			// throw new ApiAccessException(Response.Status.UNAUTHORIZED);
			// }

			// String localTimeFrom = DateTimeUtils.toTimeZone(timeFrom,
			// Defs.DATETIME_TIMEZONE_SOFIA);
			// String localTimeFromNoTz =
			// DateTimeUtils.removeTimeZone(localTimeFrom,
			// GlobalConfig.INSTANCE.getServerTimeZone());

			String localTimeFromNoTz = DateTimeUtilsNew.removeTimeZone(timeFrom, Defs.DATETIME_FORMAT);

			return getJsonResponse(
			        source.getAllCurrentRatesAfter(DateTimeUtils.parseDate(localTimeFromNoTz, Defs.DATETIME_FORMAT)));
		} catch (IOException | DataSourceException | ParseException e) {
			log.error("", e);
			return getErrorResponse();
		} catch (ApiAccessException e) {
			log.debug(e.getMessage());
			return getCustomResponse(e.getStatus());
		}
	}
}
