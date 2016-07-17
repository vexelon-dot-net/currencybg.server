package net.vexelon.currencybg.srv.db.models;

import com.google.gson.annotations.SerializedName;

/**
 * Json model representation of currency source restrictions
 * 
 * <pre>
 * { 
 * 	"wdNotBefore": "06:00", 
 * 	"wdNotAfter": "23:00", 
 * 	"weNotBefore": "06:00",
 * 	"weNotAfter": "23:00", 
 * 	"weekends": true, 
 * 	"sundays": false 
 * }
 * </pre>
 *
 */
public class SourceUpdateRestrictions {

	@SerializedName("wdNotBefore")
	private String weekdaysNotBefore;

	@SerializedName("wdNotAfter")
	private String weekdaysNotAfter;

	@SerializedName("weNotBefore")
	private String weekendsNotBefore;

	@SerializedName("weNotAfter")
	private String weekendsNotAfter;

	@SerializedName("weekends")
	private boolean enabledOnWeekends;

	@SerializedName("sundays")
	private boolean enabledOnSunday;

	private final boolean isEmpty;

	public static SourceUpdateRestrictions empty() {
		return new SourceUpdateRestrictions(true);
	}

	private SourceUpdateRestrictions(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public String getWeekdaysNotAfter() {
		return weekdaysNotAfter;
	}

	public void setWeekdaysNotAfter(String notAfter) {
		this.weekdaysNotAfter = notAfter;
	}

	public String getWeekdaysNotBefore() {
		return weekdaysNotBefore;
	}

	public void setWeekdaysNotBefore(String notBefore) {
		this.weekdaysNotBefore = notBefore;
	}

	public String getWeekendsNotAfter() {
		return weekendsNotAfter;
	}

	public void setWeekendsNotAfter(String weekendsNotAfter) {
		this.weekendsNotAfter = weekendsNotAfter;
	}

	public String getWeekendsNotBefore() {
		return weekendsNotBefore;
	}

	public void setWeekendsNotBefore(String weekendsNotBefore) {
		this.weekendsNotBefore = weekendsNotBefore;
	}

	public boolean isEnabledOnWeekends() {
		return enabledOnWeekends;
	}

	public void setEnabledOnWeekends(boolean enabledOnWeekends) {
		this.enabledOnWeekends = enabledOnWeekends;
	}

	public boolean isEnabledOnSunday() {
		return enabledOnSunday;
	}

	public void setEnabledOnSunday(boolean enabledOnSunday) {
		this.enabledOnSunday = enabledOnSunday;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	@Override
	public String toString() {
		return "SourceUpdateRestrictions [weekDaysNotBefore=" + weekdaysNotBefore + ", weekDaysNotAfter="
				+ weekdaysNotAfter + ", weekendsNotBefore=" + weekendsNotBefore + ", weekendsNotAfter="
				+ weekendsNotAfter + ", enabledOnWeekends=" + enabledOnWeekends + ", enabledOnSunday=" + enabledOnSunday
				+ ", isEmpty=" + isEmpty + "]";
	}

}
