package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

public class NullReporter extends AbstractReporter {

	public NullReporter() {
		super("Null");
	}

	@Override
	public void send() throws IOException {
	}

}
