package net.vexelon.currencybg.srv.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class UserAgentUtils {

	private final static List<String> ua = List.of(
			"Mozilla/5.0 (iPhone; CPU iPhone OS 11_0_1 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 YaBrowser/18.4.2.73.10 Mobile/15A8391 Safari/604.1",
			"Mozilla/5.0 (Linux; Android 7.0) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Focus/5.0 Chrome/66.0.3359.158 Mobile Safari/537.36",
			"Mozilla/5.0 (Linux; Android 11; Mi 11 Pro) AppleWebKit/537.46 (KHTML, like Gecko) Chrome/112.0.5615.101 Mobile Safari/537.46",
			"Mozilla/5.0 (Linux; arm_64; Android 10; CPH2125) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5495.102 Mobile Safari/537.36",
			"Mozilla/5.0 (iPhone; CPU iPhone OS 14_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.17 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4021.2 Safari/537.36 OPR/68.0.3590.0 (Edition developer)",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.17 Safari/537.36",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 5.2; Trident/6.0; .NET CLR 4.8.59447.3)");

	public static String random() {
		return ua.get(ThreadLocalRandom.current().nextInt(ua.size()));
	}
}
