package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

public class ReportData {

	private Date createdOn;
	private int source;
	private String message;

	public ReportData() {

	}

	public ReportData(Date createdOn, int source, String message) {
		this.createdOn = createdOn;
		this.source = source;
		this.message = message;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ReportData [createdOn=" + createdOn + ", source=" + source + ", message=" + message + "]";
	}

}
