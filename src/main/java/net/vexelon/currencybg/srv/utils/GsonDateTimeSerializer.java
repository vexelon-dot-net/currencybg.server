package net.vexelon.currencybg.srv.utils;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonDateTimeSerializer implements JsonSerializer<Date> {

	private ZoneId timeZone;

	public GsonDateTimeSerializer(String timeZone) {
		this.timeZone = ZoneId.of(timeZone);
	}

	@SuppressWarnings("deprecation")
	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		ZonedDateTime zdt = ZonedDateTime.of(src.getYear() + 1900, src.getMonth() + 1, src.getDate(), src.getHours(),
				src.getMinutes(), src.getSeconds(), 0, timeZone);
		return new JsonPrimitive(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
	}

}
