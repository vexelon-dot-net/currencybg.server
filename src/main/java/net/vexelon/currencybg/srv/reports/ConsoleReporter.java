package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

/**
 * Writes collected reports to {@link System#out} on {@link #send()}.
 */
public class ConsoleReporter extends AbstractReporter {

	public ConsoleReporter(String name) {
		super(name);
	}

	public ConsoleReporter() {
		this("Console");
	}

	@Override
	public void send() throws IOException {
		System.out.println(buffer.toString());
	}
}
