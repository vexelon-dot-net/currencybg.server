package net.vexelon.currencybg.srv.api;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ApiAccessException extends Exception {

	private static final long serialVersionUID = -4304582711729622152L;

	protected HttpResponseStatus status;

	public ApiAccessException(HttpResponseStatus status) {
		super(status.reasonPhrase());
		this.status = status;
	}

	public ApiAccessException(HttpResponseStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}
}
