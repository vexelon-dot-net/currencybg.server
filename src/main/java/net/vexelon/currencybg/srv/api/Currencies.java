package net.vexelon.currencybg.srv.api;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.DataSourceInterface;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

@Path("/api")
public class Currencies {

	private static final Logger log = LoggerFactory.getLogger(Currencies.class);

	@GET
	@Path("/{dateFrom}")
	public Response getAllRatesByDate(@PathParam("dateFrom") String initialDate) throws Exception {

		Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, "yyyy-MM-dd");

		String currencies = null;
		DataSourceInterface source = null;
		try {
			source = new DataSource();
			source.dbConnect();
			currencies = source.getAllRatesByDate(dateFrom);
		} catch (DataSourceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} finally {
			IOUtils.closeQuietly(source);
		}
		return Response.status(Status.OK).entity(currencies)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE + ";charset=utf-8").build();

	}

	@GET
	@Path("/nonfixed/{dateFrom}")
	public Response getNonFixedRates(@PathParam("dateFrom") String initialDate) throws Exception {

		Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, "yyyy-MM-dd");

		String currencies = null;
		DataSourceInterface source = null;
		try {
			source = new DataSource();
			source.dbConnect();
			currencies = source.getNonFixedRates(dateFrom);
		} catch (DataSourceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} finally {
			IOUtils.closeQuietly(source);
		}

		return Response.status(Status.OK).entity(currencies)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE + ";charset=utf-8").build();

	}

	@GET
	@Path("/fixed/{dateFrom}")
	public Response getFixedRates(@PathParam("dateFrom") String initialDate) throws Exception {

		Date dateFrom = DateTimeUtils.parseStringToDate(initialDate, "yyyy-MM-dd");

		String currencies = null;
		DataSourceInterface source = null;
		try {
			source = new DataSource();
			source.dbConnect();
			currencies = source.getFixedRates(dateFrom);
		} catch (DataSourceException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} finally {
			IOUtils.closeQuietly(source);
		}

		return Response.status(Status.OK).entity(currencies)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE + ";charset=utf-8").build();

	}

}
