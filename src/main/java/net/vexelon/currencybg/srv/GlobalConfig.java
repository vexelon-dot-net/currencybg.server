package net.vexelon.currencybg.srv;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import com.google.common.base.Charsets;

/**
 * 
 *
 */
public enum GlobalConfig {
	INSTANCE;

	private ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	private PeriodicReloadingTrigger trigger;

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
	 * Available configuration for Telegram bot
	 * 
	 * @author Tsvetoslav
	 *
	 */
	public enum BOT_TOKEN {
		TELEGRAM_BOT_TOKEN("telegram.bot");

		private String botToken;

		private BOT_TOKEN(String botToken) {
			this.botToken = botToken;
		}

		public String getBotToken() {
			return botToken;
		}

		public void setBotToken(String botToken) {
			this.botToken = botToken;
		}

	}

	/**
	 * Available configuration for Telegram channel
	 * 
	 * @author Tsvetoslav
	 *
	 */
	public enum CHANNEL {
		TELEGRAM_CHANNEL("telegram.channel");

		private String channel;

		private CHANNEL(String channel) {
			this.channel = channel;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

	}

	/**
	 * Set default server configurations
	 */
	public void createDefault(File file, ScheduledExecutorService executor) {
		try {
			builder = createConfigurationBuilder(file, executor);

			// defaults
			setMaintenanceEnabled(false);

			builder.save();
			builder.setAutoSave(true);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param file
	 * @param executor
	 */
	public void load(File file, ScheduledExecutorService executor) {
		builder = createConfigurationBuilder(file, executor);
		builder.setAutoSave(true);
	}

	public void close() {
		trigger.shutdown(false);
	}

	private ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> createConfigurationBuilder(File file,
			ScheduledExecutorService executor) {

		FileBasedBuilderParameters parameters = new Parameters().fileBased().setFile(file)
				.setEncoding(Charsets.UTF_8.name());

		ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder = new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(
				PropertiesConfiguration.class).configure(parameters);

		// setup reloading interval
		trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, Defs.CONFIG_RELOAD_INTERVAL,
				TimeUnit.SECONDS, executor);
		trigger.start();

		return builder;
	}

	private PropertiesConfiguration getConfig() {
		try {
			return builder.getConfiguration();
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMaintenanceEnabled() {
		return getConfig().getBoolean(Options.MAINTENANCE_ENABLED.getName());
	}

	/**
	 * 
	 * @param enabled
	 */
	public void setMaintenanceEnabled(boolean enabled) {
		getConfig().setProperty(Options.MAINTENANCE_ENABLED.getName(), enabled);
	}

	/**
	 * 
	 * @return Telegram bot token
	 */
	public String getBotToken() {
		return getConfig().getString(BOT_TOKEN.TELEGRAM_BOT_TOKEN.getBotToken());
	}

	/**
	 * 
	 * @return Telegram channel
	 */
	public String getChannel() {
		return getConfig().getString(CHANNEL.TELEGRAM_CHANNEL.getChannel());
	}

}
