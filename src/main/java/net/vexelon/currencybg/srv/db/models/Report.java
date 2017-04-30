package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

public class Report {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Date createdOn;
	private String message;

	public Report() {
	}

	public Report(Date createdOn, String message) {
		super();
		this.createdOn = createdOn;
		this.message = message;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
