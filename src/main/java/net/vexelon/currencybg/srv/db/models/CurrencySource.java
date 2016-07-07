package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

public class CurrencySource {

	private int sourceId;
	private String sourceName;
	private int status;
	private int updatePeriod;
	private Date lastUpdate;
	private SourceUpdateInfo updateInfo;

	public CurrencySource() {
	}

	public CurrencySource(int sourceId, String sourceName, int status, int updatePeriod, Date lastUpdate) {
		this.sourceId = sourceId;
		this.sourceName = sourceName;
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

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
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

	public SourceUpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(SourceUpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
	}

	@Override
	public String toString() {
		return "CurrencySource [sourceId=" + sourceId + ", sourceName=" + sourceName + ", status=" + status
				+ ", updatePeriod=" + updatePeriod + ", lastUpdate=" + lastUpdate + ", updateInfo="
				+ (updateInfo != null ? updateInfo.toString() : "") + "]";
	}

}
