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
 * Global server configurations
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
		TIMEZONE_SERVER("timezone"),
		MAINTENANCE_ENABLED("maintenance.enabled"),
		TELEGRAM_BOT_TOKEN("telegram.bot"),
		TELEGRAM_CHANNEL("telegram.channel"),
		ENABLE_LOG_SQL("log.sql"),
		ENABLE_LOG_DEBUG("log.debug"),
		SPARKPOST_API_KEY("sparkpost.apikey"),
		SPARKPOST_EMAILS("sparkpost.emails"),
		SPARKPOST_SUBJECT("sparkpost.subject"),
		SERVER_NAME("server.name");

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
	public void createDefault(File file, ScheduledExecutorService executor) {
		try {
			builder = createConfigurationBuilder(file, executor);

			// defaults
			setServerTimeZone(Defs.DATETIME_DEFAULT_TIMEZONE);
			setMaintenanceEnabled(false);
			setBotToken("");
			setBotChannel("");
			setLogSqlEnabled(false);
			setLogDebugEnabled(false);
			setSparkPostAPIKey("");
			setSparkPostEmails("");
			setSparkPostSubject("");
			setServerName("");

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
		if (trigger != null) {
			trigger.shutdown(false);
		}
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
	 * @param timeZone
	 */
	public void setServerTimeZone(String timeZone) {
		getConfig().setProperty(Options.TIMEZONE_SERVER.getName(), timeZone);
	}

	/**
	 * 
	 * @return
	 */
	public String getServerTimeZone() {
		return getConfig().getString(Options.TIMEZONE_SERVER.getName());
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
		return getConfig().getString(Options.TELEGRAM_BOT_TOKEN.getName());
	}

	/**
	 * 
	 * @param token
	 */
	public void setBotToken(String token) {
		getConfig().setProperty(Options.TELEGRAM_BOT_TOKEN.getName(), token);
	}

	/**
	 * 
	 * @return Telegram channel
	 */
	public String getBotChannel() {
		return getConfig().getString(Options.TELEGRAM_CHANNEL.getName());
	}

	/**
	 * 
	 * @param channel
	 */
	public void setBotChannel(String channel) {
		getConfig().setProperty(Options.TELEGRAM_CHANNEL.getName(), channel);
	}

	/**
	 * 
	 * @return SparkPost API KEY
	 */
	public String getSparkPostAPIKey() {
		return getConfig().getString(Options.SPARKPOST_API_KEY.getName());
	}

	/**
	 * 
	 * @param API
	 *            KEY
	 */
	public void setSparkPostAPIKey(String apiKey) {
		getConfig().setProperty(Options.SPARKPOST_API_KEY.getName(), apiKey);
	}

	/**
	 * 
	 * @return SparkPost Emails
	 */
	public String getSparkPostEmails() {
		return getConfig().getString(Options.SPARKPOST_EMAILS.getName());
	}

	/**
	 * 
	 * @param Emails
	 */
	public void setSparkPostEmails(String emails) {
		getConfig().setProperty(Options.SPARKPOST_EMAILS.getName(), emails);
	}

	/**
	 * 
	 * @return SparkPost Subject
	 */
	public String getSparkPostSubject() {
		return getConfig().getString(Options.SPARKPOST_SUBJECT.getName());
	}

	/**
	 * 
	 * @param Subject
	 */
	public void setSparkPostSubject(String subject) {
		getConfig().setProperty(Options.SPARKPOST_SUBJECT.getName(), subject);
	}

	/**
	 * 
	 * @return Server name
	 */
	public String getServerName() {
		return getConfig().getString(Options.SERVER_NAME.getName());
	}

	/**
	 * 
	 * @param Server
	 *            Name
	 */
	public void setServerName(String serverName) {
		getConfig().setProperty(Options.SERVER_NAME.getName(), serverName);
	}

	public boolean isLogSqlEnabled() {
		return getConfig().getBoolean(Options.ENABLE_LOG_SQL.getName());
	}

	/**
	 * 
	 * @param enabled
	 */
	public void setLogSqlEnabled(boolean enabled) {
		getConfig().setProperty(Options.ENABLE_LOG_SQL.getName(), enabled);
	}

	public boolean isLogDebugEnabled() {
		return getConfig().getBoolean(Options.ENABLE_LOG_DEBUG.getName());
	}

	/**
	 * 
	 * @param enabled
	 */
	public void setLogDebugEnabled(boolean enabled) {
		getConfig().setProperty(Options.ENABLE_LOG_DEBUG.getName(), enabled);
	}
}
