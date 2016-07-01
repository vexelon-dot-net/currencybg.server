package net.vexelon.currencybg.srv;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import com.google.common.base.Charsets;

/**
 * 
 *
 */
public enum GlobalConfig {
	INSTANCE;

	private PropertiesConfiguration config;

	/**
	 * Available configuration options to read/write
	 *
	 */
	public enum Options {
		MAINTENANCE_ENABLED("maintenance.enabled");

		private String optName;

		Options(String name) {
			this.optName = name;
		}

		public String getName() {
			return optName;
		}
	}

	/**
	 * Set default server configurations
	 */
	public void createDefault(File file) {
		try {
			FileBasedConfigurationBuilder<PropertiesConfiguration> builder = createConfigurationBuilder(file);
			config = builder.getConfiguration();

			// defaults
			setMaintenanceEnabled(false);

			builder.save();
			builder.setAutoSave(true);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public void load(File file) {
		try {
			FileBasedConfigurationBuilder<PropertiesConfiguration> builder = createConfigurationBuilder(file);
			config = builder.getConfiguration();
			builder.setAutoSave(true);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private FileBasedConfigurationBuilder<PropertiesConfiguration> createConfigurationBuilder(File file) {
		PropertiesBuilderParameters parameters = new Parameters().properties().setFile(file)
				.setEncoding(Charsets.UTF_8.name()).setThrowExceptionOnMissing(true)
				.setListDelimiterHandler(new DefaultListDelimiterHandler(';')).setIncludesAllowed(false);

		ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(
				PropertiesConfiguration.class).configure(parameters);

		// setup reloading interval
		PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null,
				Defs.CONFIG_RELOAD_INTERVAL, TimeUnit.SECONDS);
		trigger.start();

		return builder;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMaintenanceEnabled() {
		return config.getBoolean(Options.MAINTENANCE_ENABLED.getName());
	}

	/**
	 * 
	 * @param enabled
	 */
	public void setMaintenanceEnabled(boolean enabled) {
		config.setProperty(Options.MAINTENANCE_ENABLED.getName(), enabled);
	}

}
