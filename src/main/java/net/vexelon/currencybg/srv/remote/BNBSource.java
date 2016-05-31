package net.vexelon.currencybg.srv.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.reports.Reporter;

public class BNBSource extends AbstractSource {

	private static final Logger log = LoggerFactory.getLogger(BNBSource.class);
	private static final String TAG_NAME = BNBSource.class.getSimpleName();

	// Addresses on BNB for get on XML file
	public static final String URL_BNB_FORMAT_BG = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=BG";
	public static final String URL_BNB_FORMAT_EN = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=EN";
	public static final String URL_BNB_INDEX = "http://www.bnb.bg/index.htm";
	public static final String URL_BNB_FIXED_RATES = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERFixed/index.htm?toLang=_";

	private static final String DATE_FORMAT = "dd.MM.yyyy";

	private static final String XML_TAG_ROWSET = "ROWSET";
	private static final String XML_TAG_ROW = "ROW";
	private static final String XML_TAG_GOLD = "GOLD";
	private static final String XML_TAG_NAME = "NAME_";
	private static final String XML_TAG_CODE = "CODE";
	private static final String XML_TAG_RATIO = "RATIO";
	private static final String XML_TAG_REVERSERATE = "REVERSERATE";
	private static final String XML_TAG_RATE = "RATE";
	private static final String XML_TAG_EXTRAINFO = "EXTRAINFO";
	private static final String XML_TAG_CURR_DATE = "CURR_DATE";
	private static final String XML_TAG_TITLE = "TITLE";
	private static final String XML_TAG_F_STAR = "F_STAR";

	public BNBSource(Reporter reporter) {
		super(reporter);
	}

	/**
	 * Transforms XML data into {@link CurrencyData} models.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public List<CurrencyData> getBNBRates(InputStream input) throws IOException, XmlPullParserException {
		List<CurrencyData> result = Lists.newArrayList();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(input, Charsets.UTF_8.name());

		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		boolean isHeaderParsed = false;
		StringBuilder buffer = new StringBuilder();
		CurrencyData currencyData = new CurrencyData();

		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (XML_TAG_ROW.equalsIgnoreCase(tagName)) {
					if (isHeaderParsed) {
						currencyData = new CurrencyData();
					}
				}
				buffer.setLength(0);
				break;

			case XmlPullParser.TEXT:
				if (isHeaderParsed) {
					buffer.append(parser.getText());
					break;
				}

			case XmlPullParser.END_TAG:
				if (isHeaderParsed) {
					if (tagName.equalsIgnoreCase(XML_TAG_CODE)) {
						currencyData.setCode(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_RATIO)) {
						currencyData.setRatio(Integer.parseInt(buffer.toString()));
					} else if (tagName.equalsIgnoreCase(XML_TAG_REVERSERATE)) {
						currencyData.setBuy(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_RATE)) {
						currencyData.setSell(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_CURR_DATE)) {
						try {
							currencyData.setDate(dateFormat.parse(buffer.toString()));
						} catch (ParseException e1) {
							log.warn("Could not parse date from buffer!", e1);
							getReporter().write(TAG_NAME, "Could not parse date from buffer - '{}'! ({})",
									buffer.toString(), e1.getMessage());
							// use default (today)
							currencyData.setDate(new Date());
						}
					} else if (XML_TAG_ROW.equalsIgnoreCase(tagName)) {
						currencyData.setSource(Sources.BNB.getID());
						result.add(currencyData);
					}
				}
				if (!isHeaderParsed && XML_TAG_ROW.equalsIgnoreCase(tagName)) {
					isHeaderParsed = true;
				}
				break;

			default:
				break;
			}
			eventType = parser.next();
		}

		return result;
	}

	@Override
	public void getRates(final Callback callback) throws SourceException {
		try {
			final AbstractSource source = this;

			doGet(URL_BNB_FORMAT_EN, new HTTPCallback() {

				@Override
				public void onRequestFailed(Exception e) {
					getReporter().write(TAG_NAME, "Connection failure= {}", ExceptionUtils.getStackTrace(e));

					source.close();
					callback.onFailed(e);
				}

				@Override
				public void onRequestCompleted(HttpResponse response, boolean isCanceled) {
					List<CurrencyData> result = Lists.newArrayList();

					if (!isCanceled) {
						try {
							result = getBNBRates(response.getEntity().getContent());
						} catch (IOException e) {
							log.error("Could not parse source data!", e);
							getReporter().write(TAG_NAME, "Parse failed= {}", ExceptionUtils.getStackTrace(e));
						} catch (XmlPullParserException e) {
							log.error("Could not parse XML data!", e);
							getReporter().write(TAG_NAME, "XML parse failed= {}", ExceptionUtils.getStackTrace(e));
						}
					}

					source.close();
					callback.onCompleted(normalizeCurrencyData(result));
				}
			});
		} catch (URISyntaxException e) {
			throw new SourceException("Invalid source url - " + URL_BNB_FORMAT_EN, e);
		}
	}

	@Override
	public String getName() {
		return TAG_NAME;
	}

}
