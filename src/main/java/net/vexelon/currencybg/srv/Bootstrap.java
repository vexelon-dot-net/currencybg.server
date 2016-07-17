package net.vexelon.currencybg.srv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public class Bootstrap {

	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

	/**
	 * @param executor
	 * @throws RuntimeException
	 *             On configuration loading errors.
	 */
	public void start(ScheduledExecutorService executor) {
		log.trace("Running sanity tests ...");
		testEncoding();

		log.trace("Loading configuratons ...");
		if (StringUtils.isEmpty(Defs.CONFIG_PATH)) {
			throw new RuntimeException("Fatal error. Global configuration env variable 'CBG_CFG_PATH' not defined!");
		}

		File configFile = Paths.get(Defs.CONFIG_PATH, Defs.CONFIG_FILENAME).toFile();
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			GlobalConfig.INSTANCE.createDefault(configFile, executor);
		} else {
			GlobalConfig.INSTANCE.load(configFile, executor);
		}

		// verify configuration
		boolean zoneOK = false;
		for (String zoneId : TimeZone.getAvailableIDs()) {
			if (zoneId.equals(GlobalConfig.INSTANCE.getServerTimeZone())) {
				zoneOK = true;
				break;
			}
		}
		if (!zoneOK) {
			throw new RuntimeException(GlobalConfig.INSTANCE.getServerTimeZone() + " - time zone not found!");
		}

		if (GlobalConfig.INSTANCE.getBotToken().isEmpty()) {
			log.warn("Telegram bot token is not set!");
		}
		if (GlobalConfig.INSTANCE.getBotChannel().isEmpty()) {
			log.warn("Telegram bot channel is not set!");
		}

		log.trace("Booting threads ...");
		executor.scheduleWithFixedDelay(new Heartbeat(), Defs.UPDATE_FIRST_INTERVAL, Defs.UPDATES_PERIODIC_INTERVAL,
				TimeUnit.SECONDS);
	}

	public void stop() {
		GlobalConfig.INSTANCE.close();
	}

	private void testEncoding() {
		/*
		 * throws UnsupportedEncodingException if Java VM is started without
		 * UTF-8 support
		 */
		"Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich".getBytes(Charsets.UTF_8);
		"Любя, съешь щипцы, — вздохнёт мэр, — кайф жгуч ".getBytes(Charsets.UTF_8);
	}

}
