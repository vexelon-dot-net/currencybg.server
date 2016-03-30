package net.vexelon.currencybg.srv;

import javax.ws.rs.core.MediaType;

/**
 * Global constants
 *
 */
public final class Defs {

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

	/*
	 * Currency update parameters
	 */
	public static final long UPDATE_FIRST_INTERVAL = 10; // 10 seconds
	public static final long UPDATES_PERIODIC_INTERVAL = 6 * 60 * 60; // 6 hours

	public static final String DATETIME_FORMAT = "yyyy-MM-dd";
	public static final String API_JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE + ";charset=utf-8";

}
