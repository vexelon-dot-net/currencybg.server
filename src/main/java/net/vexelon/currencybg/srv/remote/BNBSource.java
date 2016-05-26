package net.vexelon.currencybg.srv.remote;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.Sources;

public class BNBSource implements Source {

	private static final Logger log = LoggerFactory.getLogger(BNBSource.class);

	// Addresses on BNB for get on XML file
	public final static String URL_BNB_FORMAT_BG = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=BG";
	public final static String URL_BNB_FORMAT_EN = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=EN";
	public final static String URL_BNB_INDEX = "http://www.bnb.bg/index.htm";
	public final static String URL_BNB_FIXED_RATES = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERFixed/index.htm?toLang=_";

	public final static String XML_TAG_ROWSET = "ROWSET";
	public final static String XML_TAG_ROW = "ROW";
	public final static String XML_TAG_GOLD = "GOLD";
	public final static String XML_TAG_NAME = "NAME_";
	public final static String XML_TAG_CODE = "CODE";
	public final static String XML_TAG_RATIO = "RATIO";
	public final static String XML_TAG_REVERSERATE = "REVERSERATE";
	public final static String XML_TAG_RATE = "RATE";
	public final static String XML_TAG_EXTRAINFO = "EXTRAINFO";
	public final static String XML_TAG_CURR_DATE = "CURR_DATE";
	public final static String XML_TAG_TITLE = "TITLE";
	public final static String XML_TAG_F_STAR = "F_STAR";

	public BNBSource() {
	}

	public List<CurrencyData> getBNBRates(InputStream input) throws Exception {
		List<CurrencyData> listCurrencyData = Lists.newArrayList();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(input, Charsets.UTF_8.name());

		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		CurrencyData currencyData = new CurrencyData();
		boolean isHeaderParsed = false;
		int eventType = parser.getEventType();
		StringBuilder buffer = new StringBuilder();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (XML_TAG_ROW.equalsIgnoreCase(tagName)) {
					if (isHeaderParsed) {
						currencyData = new CurrencyData();
						// defaults
						// currencyData.setRate("0");
						// currencyData.setReverseRate("0");
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
						Date currencyDate;
						try {
							currencyDate = dateFormat.parse(buffer.toString());
						} catch (ParseException e1) {
							log.debug("Could not parse date from buffer '{}'!", buffer.toString(), e1);
							// use default (today)
							currencyDate = new Date();
						}
						currencyData.setDate(currencyDate);
					} else if (XML_TAG_ROW.equalsIgnoreCase(tagName)) {
						// add to list of parsed items
						// TODO - hardcode set of some elements
						// Sources sources = Sources.BNB;
						// currencyData.setSource(Sources.BNB.getID());
						currencyData.setSource(Sources.BNB.getID());
						// currencyData.setBuy("0");
						// currencyData.setSell("0");

						listCurrencyData.add(currencyData);
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

		return listCurrencyData;
	}

	private List<CurrencyData> getBNBRates(String sourceUrl) throws SourceException {
		InputStream input = null;
		try {
			URLConnection connection = new URL(sourceUrl).openConnection();
			connection.setDoInput(true);
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// read error and throw it to caller
				input = httpConn.getErrorStream();
				throw new SourceException(new String(ByteStreams.toByteArray(input), Charsets.UTF_8.name()));
			}
			input = httpConn.getInputStream();
			return getBNBRates(input);
		} catch (Exception e) {
			throw new SourceException("Failed loading currencies from BNB source!", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	@Override
	public void getRates(Callback callback) throws SourceException {
		getBNBRates(URL_BNB_FORMAT_EN);
		// TODO Auto-generated method stub

	}

}
