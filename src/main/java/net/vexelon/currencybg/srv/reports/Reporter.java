package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

/**
 * Reporter interface
 * 
 */
public interface Reporter {

	/**
	 * Writes single message text.
	 * 
	 * @param message
	 */
	void write(String message);

	/**
	 * Writes formatted message text.
	 * 
	 * @param format
	 * @param arguments
	 */
	void write(String format, String... arguments);

	/**
	 * Sends all reported messages and clears the underlying buffer.
	 */
	void send() throws IOException;

	String getName();
}
