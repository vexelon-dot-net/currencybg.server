package net.vexelon.currencybg.srv.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import net.vexelon.currencybg.srv.Defs;

public class DateTimeUtilsNew {

	public static void main(String[] args) {
		System.out.println(removeTimeZone("2016-12-03T20:01:10+03:00", Defs.DATETIME_FORMAT));
	}

	/**
	 * Parse String to Date convert to Bulgarian Time Zone using Java 8 date
	 * formatter
	 * 
	 * @param date
	 * @param dateFormatter
	 * @return
	 */
	public static String removeTimeZone(String timeFrom, String dateTimeFormatter) {
		ZonedDateTime fromIsoDate = ZonedDateTime.parse(timeFrom);
		ZoneOffset offset = ZoneOffset.of("+02:00");
		ZonedDateTime acst = fromIsoDate.withZoneSameInstant(offset);

		// System.out.println("Input: " + fromIsoDate);
		// System.out.println("Output: " +
		// acst.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		// System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd
		// HH:mm:ss").format(acst));
		return DateTimeFormatter.ofPattern(dateTimeFormatter).format(acst);
	}
}
