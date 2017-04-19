package net.vexelon.currencybg.srv.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtilsNew {

	/**
	 * Converts String ZoneDateTime from user specific to Europe/Sofia using
	 * using Java 8 DateTime API
	 * 
	 * @param date
	 * @param dateFormatter
	 * @return
	 */
	public static String removeTimeZone(String timeFrom, String dateTimeFormatter) {
		ZonedDateTime fromIsoDate = ZonedDateTime.parse(timeFrom).withZoneSameLocal(ZoneId.of("Europe/Sofia"));
		return DateTimeFormatter.ofPattern(dateTimeFormatter).format(fromIsoDate);
	}
}
