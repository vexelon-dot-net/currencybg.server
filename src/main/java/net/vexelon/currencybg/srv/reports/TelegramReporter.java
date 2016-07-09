package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

public class TelegramReporter extends AbstractReporter {

	public TelegramReporter(String name) {
		super(name);
	}

	public TelegramReporter() {
		this("Telegram");
	}

	@Override
	public void send() throws IOException {
		System.out.println(buffer.toString());
	}

}
