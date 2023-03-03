package net.vexelon.currencybg.srv;

import com.google.common.collect.Sets;

import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Global constants
 */
public final class Defs {

    public static final String LOGGER_NAME = "net.vexelon.currencybg";

    /*
     * DateTime
     */
    public static final String DATETIME_TIMEZONE_SOFIA = "Europe/Sofia";
    public static final String DATETIME_DEFAULT_TIMEZONE = DATETIME_TIMEZONE_SOFIA;
    public static final String DATETIME_RESTR_FORMAT = "HH:mm";

    /*
     * Global configuration
     */
    public static final String CONFIG_PATH = System.getenv("CBG_CFG_PATH");
    public static final String CONFIG_FILENAME = "cbg.properties";
    public static final long CONFIG_RELOAD_INTERVAL = 10; // 10 seconds

    /*
     * Database parameters
     */
    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_NAME = System.getProperty("DB_NAME");
    public static final String DB_HOST = System.getProperty("DB_HOST");
    public static final String DB_PORT = System.getProperty("DB_PORT");
    public static final String DB_CONNECTION = String.format("jdbc:mysql://%s:%s/%s?useSSL=false", DB_HOST, DB_PORT,
            DB_NAME);
    public static final String DB_USER = System.getProperty("DB_USERNAME");
    public static final String DB_PASSWORD = System.getProperty("DB_PASSWORD");

    public static final String DATEFORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mmZ";

    /*
     * Currency update parameters
     */
    public static final long UPDATE_FIRST_INTERVAL = 10; // 10 seconds
    public static final long UPDATES_PERIODIC_INTERVAL = 5 * 60; // 5 minutes

    /*
     * Reporter thread parameters
     */
    public static final long REPORTER_UPDATE_FIRST_INTERVAL = 10;// seconds
    public static final long REPORTER_UPDATES_PERIODIC_INTERVAL = 60 * 60;// 1
    // hour

    /*
     * HTTP API specific
     */
    public static final String HEADER_APIKEY = "APIKey";
    public static final String API_JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE + ";charset=utf-8";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // public static final String DATETIME_FORMAT_ISO8601 =
    // "yyyy-MM-dd'T'HH:mm:ssZ";

    /*
     * Currencies
     */
    public static final String CURRENCY_BITCOIN = "BTC";
    public static final String CURRENCY_BITCOIN_CASH = "BCH";
    public static final String CURRENCY_ETHERIUM = "ETH";
    public static final String CURRENCY_EOS = "EOS";
//    public static final String CURRENCY_ETHERIUM_CLASSIC = "ETC";
    public static final String CURRENCY_LITECOIN = "LTC";
    public static final String CURRENCY_DASH = "DASH";
    public static final String CURRENCY_DOGECOIN = "DOGE";
    public static final String CURRENCY_RIPPLE = "XRP";
    public static final String CURRENCY_ZCASH = "ZEC";
    public static final String CURRENCY_MONERO = "XMR";
    public static final String CURRENCY_STELLAR = "XLM";

    /**
     * Currency codes recognized by the mobile app
     */
    public static final Set<String> CURRENCY_CODES_APP = Sets.newHashSet("AED",
            "ALL",
            "ARS",
            "AUD",
            "AZN",
            "BAM",
            "BCH",
            "BGN",
            "BRL",
            "BTC",
            "BYN",
            "BYR",
            "CAD",
            "CHF",
            "CNH",
            "CNY",
            "CZK",
            "DASH",
            "DKK",
            "DOGE",
            "DOP",
            "EEK",
            "EGP",
            "ETC",
            "ETH",
            "EUR",
            "GEL",
            "GBP",
            "HKD",
            "HRK",
            "HUF",
            "IDR",
            "INR",
            "ILS",
            "ISK",
            "JOD",
            "JPY",
            "KES",
            "KRW",
            "LTC",
            "LTL",
            "LVL",
            "MDL",
            "MKD",
            "MUR",
            "MXN",
            "MYR",
            "NOK",
            "NZD",
            "PHP",
            "PLN",
            "QAR",
            "RON",
            "RSD",
            "RUB",
            "SAR",
            "SEK",
            "SGD",
            "THB",
            "TND",
            "TRY",
            "TWD",
            "UAH",
            "USD",
            "VND",
            "XLM",
            "XMR",
            "XRP",
            "ZAR",
            "ZEC");
}
