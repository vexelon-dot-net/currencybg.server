package net.vexelon.currencybg.srv.utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.vexelon.currencybg.srv.Defs;

public class GsonDateTimeSerializer implements JsonSerializer<Date> {

	private String timeZone;

	public GsonDateTimeSerializer(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		try {
			return new JsonPrimitive(DateTimeUtils.toStringISO8601(src, timeZone));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// fall back variant
		return new JsonPrimitive(new SimpleDateFormat(Defs.DATEFORMAT_ISO_8601).format(src));
	}

}
