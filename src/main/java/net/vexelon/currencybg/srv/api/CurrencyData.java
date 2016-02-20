package net.vexelon.currencybg.srv.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/currencydata")
public class CurrencyData {

	private static final Logger log = LoggerFactory.getLogger(CurrencyData.class);

	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_NAME = "currencybg";
	private static final String DB_HOST = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
	private static final String DB_PORT = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
	private static final String DB_CONNECTION = String.format("jdbc:mysql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
	private static final String DB_USER = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
	private static final String DB_PASSWORD = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");

	@GET
	@Produces("text/plain" + ";charset=utf-8")
	public String InspectionData(/* @QueryParam("policyNo") String policyNo */) throws Exception {

		String inspections;

		inspections = "Database data: " + selectRecordsFromTable();

		return inspections;

	}

	private static String selectRecordsFromTable() throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		String selectSQL = "SELECT ID FROM temp";
		StringBuffer result = new StringBuffer();

		// XXX test
		log.trace("Selected rows {} in {}", selectSQL, selectSQL);

		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			// preparedStatement.setInt(1, 1001);

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				// String userid = rs.getString("USER_ID");
				String id = rs.getString("ID");

				result.append(id);
				// System.out.println("userid : " + userid);
				// System.out.println("ID : " + id);

			}

		} catch (SQLException e) {
			log.error("Error selecting rows!", e); // XXX test

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return result.toString();
	}

	private static Connection getDBConnection() {

		Connection dbConnection = null;

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {

			System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
}
