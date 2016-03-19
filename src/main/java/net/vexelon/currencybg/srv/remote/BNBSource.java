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
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencyLocales;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;

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

	public List<CurrencyData> getRates(InputStream input) throws Exception {
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
						currencyData.setRate("0");
						currencyData.setReverseRate("0");
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
					if (tagName.equalsIgnoreCase(XML_TAG_GOLD)) {
						currencyData.setGold(Integer.parseInt(buffer.toString()));
					} else if (tagName.equalsIgnoreCase(XML_TAG_NAME)) {
						currencyData.setName(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_CODE)) {
						currencyData.setCode(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_RATIO)) {
						currencyData.setRatio(Integer.parseInt(buffer.toString()));
					} else if (tagName.equalsIgnoreCase(XML_TAG_REVERSERATE)) {
						currencyData.setReverseRate(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_RATE)) {
						currencyData.setRate(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_EXTRAINFO)) {
						currencyData.setExtraInfo(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_CURR_DATE)) {
						Date currencyDate;
						try {
							currencyDate = dateFormat.parse(buffer.toString());
						} catch (ParseException e1) {
							log.debug("Could not parse date from buffer '{}'!", buffer.toString(), e1);
							// use default (today)
							currencyDate = new Date();
						}
						currencyData.setCurrDate(currencyDate);
					} else if (tagName.equalsIgnoreCase(XML_TAG_TITLE)) {
						currencyData.setTitle(buffer.toString());
					} else if (tagName.equalsIgnoreCase(XML_TAG_F_STAR)) {
						currencyData.setfStar(Integer.parseInt(buffer.toString()));
					} else if (XML_TAG_ROW.equalsIgnoreCase(tagName)) {
						// add to list of parsed items
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

	private List<CurrencyData> getRates(String sourceUrl) throws SourceException {
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
			return getRates(input);
		} catch (Exception e) {
			throw new SourceException("Failed loading currencies from BNB source!", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public List<CurrencyData> getFixedRates(InputStream input, String baseUri) throws Exception {
		List<CurrencyData> listFixedCurrencyData = Lists.newArrayList();
		CurrencyData fixedCurrencyData = new CurrencyData();
		Date currentYear = DateTimeUtils.getStartOfYear();

		Document doc = Jsoup.parse(input, Charsets.UTF_8.name(), baseUri);

		// Element element =
		// doc.select("div#more_information > div.box > div.top > div > ul >
		// li").first();
		Element div = doc.select("div#content_box.content > div.doc_entry > div > table > tbody").first();
		Elements divChildren = div.children();

		int lineNumber = 1;
		for (Element table : divChildren) {
			if (lineNumber > 1) {
				// System.out.println(table.tagName());
				Elements tableChildren = table.children();
				int elementNumber = 1;
				fixedCurrencyData.setGold(1);
				fixedCurrencyData.setfStar(0);
				fixedCurrencyData.setCurrDate(currentYear);
				fixedCurrencyData.setIsFixed(true);
				for (Element elem : tableChildren) {
					// System.out.println(elem.tagName());
					Element elemChild = elem.children().first();
					// System.out.print(elemChild.text());//
					// elemChild.text()
					switch (elementNumber) {
					case 1:
						fixedCurrencyData.setName(elemChild.text());
						break;
					case 2:
						fixedCurrencyData.setCode(elemChild.text());
						break;
					case 3:
						fixedCurrencyData.setRatio(Integer.parseInt(elemChild.text()));
						break;
					case 4:
						fixedCurrencyData.setRate(elemChild.text());
						break;
					case 5:
						fixedCurrencyData.setReverseRate(elemChild.text());
						break;
					}
					elementNumber++;
				}
				listFixedCurrencyData.add(fixedCurrencyData);
				fixedCurrencyData = new CurrencyData();
			}
			lineNumber++;
		}
		// Element euroValue = element.getElementsByTag("strong").first();
		// String euroValuReturn = euroValue.text();
		return listFixedCurrencyData;
	}

	private List<CurrencyData> getFixedRates(String sourceUrl, String language) throws SourceException {
		InputStream input = null;
		try {
			URLConnection connection = new URL(sourceUrl).openConnection();
			connection.setDoInput(true);
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			httpConn.setRequestProperty("Cookie", "userLanguage=" + language);
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// read error and throw it to caller
				input = httpConn.getErrorStream();
				throw new SourceException(new String(ByteStreams.toByteArray(input), Charsets.UTF_8.name()));
			}
			input = httpConn.getInputStream();
			return getFixedRates(input, sourceUrl);
		} catch (Exception e) {
			throw new SourceException("Failed loading fixed currencies from BNB source!", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	@Override
	public Map<CurrencyLocales, List<CurrencyData>> downloadRates() throws SourceException {
		Map<CurrencyLocales, List<CurrencyData>> result = Maps.newHashMap();

		List<CurrencyData> ratesENList = getRates(URL_BNB_FORMAT_EN);
		ratesENList.addAll(getFixedRates(URL_BNB_FIXED_RATES + CurrencyLocales.EN.name(), CurrencyLocales.EN.name()));
		result.put(CurrencyLocales.EN, ratesENList);

		List<CurrencyData> ratesBGList = getRates(URL_BNB_FORMAT_BG);
		ratesBGList.addAll(getFixedRates(URL_BNB_FIXED_RATES + CurrencyLocales.BG.name(), CurrencyLocales.BG.name()));
		result.put(CurrencyLocales.BG, ratesBGList);

		return result;
	}

}
