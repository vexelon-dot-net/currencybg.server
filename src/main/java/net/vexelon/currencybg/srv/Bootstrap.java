package net.vexelon.currencybg.srv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public class Bootstrap {

	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

	/**
	 * 
	 * @throws RuntimeException
	 *             On configuration loading errors.
	 */
	public void init() {
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

			GlobalConfig.INSTANCE.createDefault(configFile);
		} else {
			GlobalConfig.INSTANCE.load(configFile);
		}
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
