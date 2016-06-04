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
	 * @param tag
	 *            Context name under which {@code message} is being reported.
	 * @param message
	 */
	void write(String tag, String message);

	/**
	 * Writes formatted message text.
	 * 
	 * @param tag
	 *            Context name under which {@code message} is being reported.
	 * @param format
	 *            Formatted arguments must be of type {@link String}, e.g.,
	 *            {@code "Item={}, Time={}"}
	 * @param arguments
	 */
	void write(String tag, String format, String... arguments);

	/**
	 * Sends all reported messages and clears the underlying buffer.
	 */
	void send() throws IOException;

	/**
	 * 
	 * @return name of the {@link Reporter}.
	 */
	String getName();

	/**
	 * 
	 * @return {@code true}, if the Reporter does not contain any messages.
	 */
	boolean isEmpty();
}
