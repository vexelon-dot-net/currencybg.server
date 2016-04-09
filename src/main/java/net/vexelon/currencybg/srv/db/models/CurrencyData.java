package net.vexelon.currencybg.srv.db.models;

import java.util.Date;

public class CurrencyData {

	private int gold;
	private String name;
	private String code;
	private int ratio;
	private String reverseRate;
	private String rate;
	private String extraInfo;
	private Date currDate;
	private String title;
	private int fStar;
	private String location;
	private boolean isFixed;

	public CurrencyData() {
	}

	public CurrencyData(int gold, String name, String code, int ratio, String reverseRate, String rate,
			String extraInfo, Date currDate, String title, int fStar, String location, boolean isFixed) {

		this.gold = gold;
		this.name = name;
		this.code = code;
		this.ratio = ratio;
		this.reverseRate = reverseRate;
		this.rate = rate;
		this.extraInfo = extraInfo;
		this.currDate = currDate;
		this.title = title;
		this.fStar = fStar;
		this.location = location;
		this.isFixed = isFixed;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getReverseRate() {
		return reverseRate;
	}

	public void setReverseRate(String reverseRate) {
		this.reverseRate = reverseRate;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public Date getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getfStar() {
		return fStar;
	}

	public void setfStar(int fStar) {
		this.fStar = fStar;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setIsFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}

	@Override
	public String toString() {
		return "CurrencyData [gold=" + gold + ", name=" + name + ", code=" + code + ", ratio=" + ratio
				+ ", reverseRate=" + reverseRate + ", rate=" + rate + ", extraInfo=" + extraInfo + ", currDate="
				+ currDate + ", title=" + title + ", fStar=" + fStar + ", location=" + location + ", isFixed=" + isFixed
				+ "]";
	}

}
