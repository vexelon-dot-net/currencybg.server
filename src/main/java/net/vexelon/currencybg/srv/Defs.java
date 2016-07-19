package net.vexelon.currencybg.srv;

import javax.ws.rs.core.MediaType;

/**
 * Global constants
 *
 */
public final class Defs {

	/*
	 * DateTime
	 */
	public static final String DATETIME_DEFAULT_TIMEZONE = "Europe/Sofia";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_RESTR_FORMAT = "HH:mm";

	/*
	 * Global configuration
	 */
	public static final String CONFIG_PATH = System.getenv("CBG_CFG_PATH");
	public static final String CONFIG_FILENAME = "cbg.properties";
	public static final long CONFIG_RELOAD_INTERVAL = 10; // 10 seconds

	/*
	 * Database parameters
	 */
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_NAME = "currencybg";
	public static final String DB_HOST = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
	public static final String DB_PORT = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
	public static final String DB_CONNECTION = String.format("jdbc:mysql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
	public static final String DB_USER = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
	public static final String DB_PASSWORD = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");

	public static final String DATEFORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mmZ";

	/*
	 * Currency update parameters
	 */
	public static final long UPDATE_FIRST_INTERVAL = 10; // 10 seconds
	public static final long UPDATES_PERIODIC_INTERVAL = 5 * 60; // 5 minutes

	/*
	 * HTTP API specific
	 */
	public static final String HEADER_APIKEY = "APIKey";
	public static final String API_JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE + ";charset=utf-8";

<<<<<<< HEAD
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

=======
>>>>>>> 56386dda70d81386c7604ec8c25c153601cc3d0f
}