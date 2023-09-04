package net.vexelon.currencybg.srv;

import com.google.common.base.Charsets;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Global server configurations
 */
public enum GlobalConfig {
	INSTANCE;

	private ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	private PeriodicReloadingTrigger                                        trigger;

	/**
	 * Available configuration options to read/write
	 */
	public enum Options {
		SERVER_NAME("server.name"),
		TIMEZONE_SERVER("timezone"),
		ENABLE_LOG_DEBUG("log.debug"),
		CLEANUP_INTERVAL("cleanup.interval"),
		TELEGRAM_BOT_TOKEN("telegram.bot"),
		TELEGRAM_CHANNEL("telegram.channel"),
		SPARKPOST_API_KEY("sparkpost.apikey"),
		SPARKPOST_EMAILS("sparkpost.emails"),
		SPARKPOST_SUBJECT("sparkpost.subject"),
		SPARKPOST_FROM("sparkpost.from"),
		REPORT_TYPE("reporter.type");

		private final String optName;

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
			setServerName("");
			setServerTimeZone(Defs.DATETIME_DEFAULT_TIMEZONE);
			setLogDebugEnabled(false);
			setCleanupInterval(Defs.DEFAULT_CLEANUP_INTERVAL);
			setBotToken("");
			setBotChannel("");
			setSparkPostAPIKey("");
			setSparkPostEmails("");
			setSparkPostSubject("");
			setSparkPostFrom("");
			setReportType("");

			builder.save();
			builder.setAutoSave(true);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

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
		var parameters = new Parameters().fileBased().setFile(file).setEncoding(Charsets.UTF_8.name());
		var builder = new ReloadingFileBasedConfigurationBuilder<>(PropertiesConfiguration.class).configure(parameters);

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
	 * @return Server name
	 */
	public String getServerName() {
		return getConfig().getString(Options.SERVER_NAME.getName());
	}

	public void setServerName(String serverName) {
		getConfig().setProperty(Options.SERVER_NAME.getName(), serverName);
	}

	public void setServerTimeZone(String timeZone) {
		getConfig().setProperty(Options.TIMEZONE_SERVER.getName(), timeZone);
	}

	public String getServerTimeZone() {
		return getConfig().getString(Options.TIMEZONE_SERVER.getName());
	}

	public boolean isLogDebugEnabled() {
		return getConfig().getBoolean(Options.ENABLE_LOG_DEBUG.getName());
	}

	public void setLogDebugEnabled(boolean enabled) {
		getConfig().setProperty(Options.ENABLE_LOG_DEBUG.getName(), enabled);
	}

	public int getCleanupInterval() {
		return getConfig().getInt(Options.CLEANUP_INTERVAL.getName(), Defs.DEFAULT_CLEANUP_INTERVAL);
	}

	public void setCleanupInterval(int intervalInDays) {
		getConfig().setProperty(Options.CLEANUP_INTERVAL.getName(), intervalInDays);
	}

	/**
	 * @return Telegram bot token
	 */
	public String getBotToken() {
		return getConfig().getString(Options.TELEGRAM_BOT_TOKEN.getName());
	}

	public void setBotToken(String token) {
		getConfig().setProperty(Options.TELEGRAM_BOT_TOKEN.getName(), token);
	}

	/**
	 * @return Telegram channel
	 */
	public String getBotChannel() {
		return getConfig().getString(Options.TELEGRAM_CHANNEL.getName());
	}

	public void setBotChannel(String channel) {
		getConfig().setProperty(Options.TELEGRAM_CHANNEL.getName(), channel);
	}

	/**
	 * @return SparkPost API KEY
	 */
	public String getSparkPostAPIKey() {
		return getConfig().getString(Options.SPARKPOST_API_KEY.getName());
	}

	public void setSparkPostAPIKey(String apiKey) {
		getConfig().setProperty(Options.SPARKPOST_API_KEY.getName(), apiKey);
	}

	/**
	 * @return SparkPost Emails
	 */
	public String getSparkPostEmails() {
		return getConfig().getString(Options.SPARKPOST_EMAILS.getName());
	}

	public void setSparkPostEmails(String emails) {
		getConfig().setProperty(Options.SPARKPOST_EMAILS.getName(), emails);
	}

	/**
	 * @return SparkPost Subject
	 */
	public String getSparkPostSubject() {
		return getConfig().getString(Options.SPARKPOST_SUBJECT.getName());
	}

	public void setSparkPostSubject(String subject) {
		getConfig().setProperty(Options.SPARKPOST_SUBJECT.getName(), subject);
	}

	/**
	 * @return SparkPost From - Domain which will be used by SparkPost to send
	 * notifications for errors
	 */
	public String getSparkPostFrom() {
		return getConfig().getString(Options.SPARKPOST_FROM.getName());
	}

	public void setSparkPostFrom(String from) {
		getConfig().setProperty(Options.SPARKPOST_FROM.getName(), from);
	}

	/**
	 * @return Report type. For instance: SparkPost or Telegram
	 */
	public String getReportType() {
		return getConfig().getString(Options.REPORT_TYPE.getName());
	}

	public void setReportType(String reportType) {
		getConfig().setProperty(Options.REPORT_TYPE.getName(), reportType);
	}
}
