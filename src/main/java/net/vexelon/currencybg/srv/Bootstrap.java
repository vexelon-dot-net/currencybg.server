package net.vexelon.currencybg.srv;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public class Bootstrap {

	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

	public void init() {
		log.trace("Running sanity tests ...");

		testEncoding();
	}

	private void loadConfiguration() {
		PropertiesBuilderParameters parameters = new Parameters().properties().setFileName("myconfig.properties")
				.setThrowExceptionOnMissing(true).setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
				.setIncludesAllowed(false);

		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
				PropertiesConfiguration.class).configure(parameters);

		try {
			PropertiesConfiguration config = builder.getConfiguration();
			config.setProperty("maintenance.enabled", false);

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
