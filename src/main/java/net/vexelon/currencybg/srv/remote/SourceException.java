package net.vexelon.currencybg.srv.remote;

public class SourceException extends Exception {

	private static final long serialVersionUID = -406613277075607505L;

	public SourceException(String message) {
		super(message);
	}

	public SourceException(Throwable t) {
		super(t);
	}

	public SourceException(String message, Throwable t) {
		super(message, t);
	}
}
