package net.vexelon.currencybg.srv.db.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

import net.vexelon.currencybg.srv.utils.DateTimeUtils;

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

	public static final String TIME_FORMAT = "HH:mm";

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

	public String getWeekdaysNotAfter() {
		return weekdaysNotAfter;
	}

	/**
	 * 
	 * @return week day time and minute at which to stop updating
	 * @throws ParseException
	 */
	public Calendar getWeekdaysNotAfterCalendar() throws ParseException {
		return parseTime(weekdaysNotAfter);
	}

	public void setWeekdaysNotAfter(String notAfter) {
		this.weekdaysNotAfter = notAfter;
	}

	public String getWeekdaysNotBefore() {
		return weekdaysNotBefore;
	}

	/**
	 * 
	 * @return week day time and minute at which to start updating
	 * @throws ParseException
	 */
	public Calendar getWeekdaysNotBeforeCalendar() throws ParseException {
		return parseTime(weekdaysNotBefore);
	}

	public void setWeekdaysNotBefore(String notBefore) {
		this.weekdaysNotBefore = notBefore;
	}

	public String getWeekendsNotAfter() {
		return weekendsNotAfter;
	}

	/**
	 * 
	 * @return weekend time and minute at which to stop updating
	 * @throws ParseException
	 */
	public Calendar getWeekendsNotAfterCalendar() throws ParseException {
		return parseTime(weekendsNotAfter);
	}

	public void setWeekendsNotAfter(String weekendsNotAfter) {
		this.weekendsNotAfter = weekendsNotAfter;
	}

	public String getWeekendsNotBefore() {
		return weekendsNotBefore;
	}

	/**
	 * 
	 * @return weekend time and minute at which to start updating
	 * @throws ParseException
	 */
	public Calendar getWeekendsNotBeforeCalendar() throws ParseException {
		return parseTime(weekendsNotBefore);
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
