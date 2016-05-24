package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

public class CurrencySource {

	private int sourceId;
	private int status;
	private int updatePeriod;
	private Date lastUpdate;

	public CurrencySource() {

	}

	public CurrencySource(int sourceId, int status, int updatePeriod, Date lastUpdate) {
		super();
		this.sourceId = sourceId;
		this.status = status;
		this.updatePeriod = updatePeriod;
		this.lastUpdate = lastUpdate;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUpdatePeriod() {
		return updatePeriod;
	}

	public void setUpdatePeriod(int updatePeriod) {
		this.updatePeriod = updatePeriod;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "CurrencySource [sourceId=" + sourceId + ", status=" + status + ", updatePeriod=" + updatePeriod
				+ ", lastUpdate=" + lastUpdate + "]";
	}

}
