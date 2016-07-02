package net.vexelon.currencybg.srv.api;

import javax.ws.rs.core.Response;

/**
 *  
 *
 */
public class ApiAccessException extends Exception {

	private static final long serialVersionUID = -4304582711729622152L;

	protected Response.Status status;

	public ApiAccessException(Response.Status status) {
		super(status.getReasonPhrase());
		this.status = status;
	}

	public ApiAccessException(Response.Status status, String message) {
		super(message);
		this.status = status;
	}

	public Response.Status getStatus() {
		return status;
	}
}
