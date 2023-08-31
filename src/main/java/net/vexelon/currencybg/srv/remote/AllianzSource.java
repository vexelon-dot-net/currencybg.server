package net.vexelon.currencybg.srv.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllianzSource extends AbstractSource {

	private static final Logger log      = LoggerFactory.getLogger(AllianzSource.class);
	private static final String TAG_NAME = AllianzSource.class.getSimpleName();

	private static final String URL_SOURCE        = "https://remote.bank.allianz.bg/EBank/Common";
	private static final String HTTP_CONTENT_TYPE = "text/xml";
	private static final String HTTP_REQUEST_BODY = "<COMMON lang=\"BG\"><EXCHANGERATES/></COMMON>";

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private static final String XML_TAG_ROOT          = "COMMON";
	private static final String XML_TAG_SUBROOT       = "EXCHANGERATES";
	private static final String XML_TAG_ROW           = "EXCHANGERATE";
	private static final String XML_TAG_ATTR_CODE     = "currid";
	private static final String XML_TAG_ATTR_DATETIME = "dtstamp";
	private static final String XML_TAG_ATTR_TYPE     = "type";
	private static final String XML_TAG_ATTR_RATIO    = "units";
	private static final String XML_TAG_ATTR_BUY      = "buy";
	private static final String XML_TAG_ATTR_SELL     = "sell";

	public AllianzSource(Reporter reporter) {
		super(reporter);
	}

	public List<CurrencyData> getAllianzRates(InputStream input) throws IOException, XmlPullParserException {
		List<CurrencyData> result = new ArrayList<>();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(input, Charsets.UTF_8.name());

		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();

			switch (eventType) {
				case XmlPullParser.START_TAG:
					if (XML_TAG_ROW.equalsIgnoreCase(tagName)) {
						if ("S".equalsIgnoreCase(parser.getAttributeValue("", XML_TAG_ATTR_TYPE))) {
							CurrencyData currencyData = new CurrencyData();
							currencyData.setSource(Sources.ALLIANZ.getID());
							try {
								currencyData.setCode(parser.getAttributeValue("", XML_TAG_ATTR_CODE));
								currencyData.setBuy(parser.getAttributeValue("", XML_TAG_ATTR_BUY));
								currencyData.setSell(parser.getAttributeValue("", XML_TAG_ATTR_SELL));
								currencyData.setRatio(
										Integer.parseInt(parser.getAttributeValue("", XML_TAG_ATTR_RATIO)));

								// XML data does not contain timestamp, only date values.
								Date parsed = dateFormat.parse(parser.getAttributeValue("", XML_TAG_ATTR_DATETIME));
								Date now = new Date();
								if (now.getYear() != parsed.getYear()) {
									log.warn("Skipped '{}'. Currency date too old - {}", currencyData.getCode(),
											parsed.toString());
									break;
								}

								now.setYear(parsed.getYear());
								now.setMonth(parsed.getMonth());
								now.setSeconds(parsed.getSeconds());
								currencyData.setDate(now);

								result.add(currencyData);
							} catch (Throwable t) {
								log.warn("Could not parse Allianz currency!", t);
								getReporter().write(TAG_NAME, "Could not parse Allianz currency - '{}'! ({})",
										currencyData.toString(), t.getMessage());
							}
						}
					}
					break;
			}

			eventType = parser.next();
		}

		return normalizeCurrencyData(result);
	}

	@Override
	public void getRates(final Callback callback) throws SourceException {
		try {
			doPost(URL_SOURCE, HTTP_REQUEST_BODY, HTTP_CONTENT_TYPE, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

					AllianzSource.this.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							result = getAllianzRates(response.getEntity().getContent());
						} catch (IOException | XmlPullParserException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					} else {
						log.warn("Request was canceled! No currencies were downloaded.");
						getReporter().write(TAG_NAME, "Request was canceled! No currencies were downloaded.");
					}

					AllianzSource.this.close();
					callback.onCompleted(result);

				}
			});

		} catch (URISyntaxException e) {
			throw new SourceException("Invalid source url - " + URL_SOURCE, e);
		}
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}

}
