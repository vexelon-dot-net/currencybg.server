package net.vexelon.currencybg.srv.db.models;

public class APIKey {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String keyValue;
	private Integer status;

	public APIKey() {
	}

	public APIKey(String keyValue, Integer status) {
		super();
		this.keyValue = keyValue;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
