package net.vexelon.currencybg.srv.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class UserAgentUtils {

	private final static List<String> ua = List.of(
			"Mozilla/5.0 (iPhone; CPU iPhone OS 11_0_1 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 YaBrowser/18.4.2.73.10 Mobile/15A8391 Safari/604.1",
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36 Maxthon/5.2.3.500",
			"Mozilla/5.0 (Linux; Android 7.0) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Focus/5.0 Chrome/66.0.3359.158 Mobile Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Iridium/2018.4 Safari/537.36 Chrome/66.0.3359.130",
			"Mozilla/5.0 (Linux; Android 11; Mi 11 Pro) AppleWebKit/537.46 (KHTML, like Gecko) Chrome/112.0.5615.101 Mobile Safari/537.46",
			"Mozilla/5.0 (Linux; arm_64; Android 10; CPH2125) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5495.102 Mobile Safari/537.36",
			"Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.4882.400 QQBrowser/9.7.13036.400",
			"Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36 OPR/32.0.1948.69 (Edition Yx)",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.0 Safari/CE89F3",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.17 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4021.2 Safari/537.36 OPR/68.0.3590.0 (Edition developer)",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.17 Safari/537.36",
			"Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/531.2.0 (KHTML, like Gecko) Chrome/34.0.871.0 Safari/531.2.0",
			"Mozilla/5.0 (Windows; U; Windows NT 6.2) AppleWebKit/534.1.0 (KHTML, like Gecko) Chrome/36.0.868.0 Safari/534.1.0",
			"Mozilla/5.0 (Windows NT 5.1; Win64; x64; rv:15.1) Gecko/20100101 Firefox/15.1.8",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 5.2; Trident/6.0; .NET CLR 4.8.59447.3)",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

	public static String random() {
		return ua.get(ThreadLocalRandom.current().nextInt(ua.size()));
	}
}
