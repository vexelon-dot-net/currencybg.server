package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

public class ReportData {

	private int    id; // SQL
	private String documentId; // Firestore
	private Date   createdOn;
	//	private int    source;
	private String message;

	public ReportData() {

	}

	public ReportData(int id, Date createdOn, String message) {
		super();
		this.id = id;
		this.createdOn = createdOn;
		//		this.source = source;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	//	public int getSource() {
	//		return source;
	//	}
	//
	//	public void setSource(int source) {
	//		this.source = source;
	//	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ReportData{" + "id=" + id + ", documentId='" + documentId + '\'' + ", createdOn=" + createdOn
				+ ", message='" + message + '\'' + '}';
	}
}
