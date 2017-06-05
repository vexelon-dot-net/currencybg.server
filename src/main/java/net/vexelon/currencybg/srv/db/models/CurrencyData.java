package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

import javax.persistence.Transient;

public class CurrencyData {

	private transient Integer id;
	private String code;
	private int ratio = 0; // default
	private String buy = "0"; // default
	private String sell = "0"; // default
	private Date date;
	private int source;

	public CurrencyData() {
	}

	public CurrencyData(String code, int ratio, String buy, String sell, Date date, int source) {
		super();
		this.code = code;
		this.ratio = ratio;
		this.buy = buy;
		this.sell = sell;
		this.date = date;
		this.source = source;
	}

	@Transient
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public String getBuy() {
		return buy;
	}

	public void setBuy(String buy) {
		this.buy = buy;
	}

	public String getSell() {
		return sell;
	}

	public void setSell(String sell) {
		this.sell = sell;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "CurrencyData [code=" + code + ", ratio=" + ratio + ", buy=" + buy + ", sell=" + sell + ", date=" + date
		        + ", source=" + source + "]";
	}

}
