package net.vexelon.currencybg.srv;

import org.hibernate.Session;

public class CloseableSession implements AutoCloseable {
	private final Session session;

	public CloseableSession(Session session) {
		this.session = session;
	}

	public Session delegate() {
		return session;
	}

	@Override
	public void close() {
		session.close();
	}
}
