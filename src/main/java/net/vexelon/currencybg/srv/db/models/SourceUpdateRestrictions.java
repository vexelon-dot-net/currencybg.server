package net.vexelon.currencybg.srv.db.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import net.vexelon.currencybg.srv.utils.DateTimeUtils;

public class SourceUpdateRestrictions {

	public static final String TIME_FORMAT = "HH:mm";

	@SerializedName("notBefore")
	private String notBefore;

	@SerializedName("notAfter")
	private String notAfter;

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

	private Calendar parseTime(String value) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
		Date date = dateFormat.parse(value);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		Calendar today = DateTimeUtils.getCalToday();
		calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, today.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, today.get(Calendar.DATE));

		return calendar;
	}

	public String getNotAfter() {
		return notAfter;
	}

	/**
	 * 
	 * @return today's time and minute at which to stop updating
	 * @throws ParseException
	 */
	public Calendar getNotAfterCalendar() throws ParseException {
		return parseTime(notAfter);
	}

	public void setNotAfter(String notAfter) {
		this.notAfter = notAfter;
	}

	public String getNotBefore() {
		return notBefore;
	}

	/**
	 * 
	 * @return today's time and minute at which to start updating
	 * @throws ParseException
	 */
	public Calendar getNotBeforeCalendar() throws ParseException {
		return parseTime(notBefore);
	}

	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
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
		return "SourceUpdateInfo [notBefore=" + notBefore + ", notAfter=" + notAfter + ", enabledOnWeekends="
				+ enabledOnWeekends + ", enabledOnSunday=" + enabledOnSunday + "]";
	}

}
