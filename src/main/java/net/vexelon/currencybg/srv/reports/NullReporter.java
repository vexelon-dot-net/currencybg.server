package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

/**
 * Dummy reports collector. Does not send any reports on {@link #send()}.
 *
 */
public class NullReporter extends AbstractReporter {

	public NullReporter(String name) {
		super(name);
	}

	public NullReporter() {
		this("Null");
	}

	@Override
	public void send() throws IOException {
		// do nothing
	}

	@Override
	public void write(String tag, String format, String... arguments) {
		// do nothing
	}

	@Override
	public void write(String tag, String message) {
		// do nothing
	}

}
