package net.vexelon.currencybg.srv;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Charsets;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.vertx.core.Vertx;
import net.vexelon.currencybg.srv.db.FirestoreDataSource;
import net.vexelon.currencybg.srv.reports.ReporterHeartbeat;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Bootstrap {

	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	private final        Vertx  vertx;

	public Bootstrap(Vertx vertx) {
		this.vertx = vertx;
	}

	/**
	 * @throws RuntimeException On configuration loading errors.
	 */
	public void start(ScheduledExecutorService executor) {
		logConsole("Running sanity tests ...");
		testEncoding();

		logConsole("Loading configurations ...");

		File configFile;
		if (StringUtils.isBlank(Defs.CONFIG_PATH)) {
			logConsole(
					"Global configuration env variable 'CBG_CFG_PATH' not defined. Trying to load 'cbg.properties' from resources...");
			String prefix = "cbg_%s_properties".formatted(RandomStringUtils.randomAlphanumeric(7));
			try {
				configFile = Files.createTempFile(prefix, null).toFile();
				try (var input = Bootstrap.class.getResourceAsStream("/cbg.properties");
						var output = new FileOutputStream(configFile)) {
					IOUtils.copy(Objects.requireNonNull(input, "resources cbg.properties is <null>"), output);
				}
			} catch (IOException e) {
				throw new RuntimeException("Fatal error: Failed to create/load from temp: %s".formatted(prefix), e);
			}
		} else {
			configFile = Paths.get(Defs.CONFIG_PATH, Defs.CONFIG_FILENAME).toFile();
		}

		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Fatal error: Cannot open configuration file: %s".formatted(configFile), e);
			}

			GlobalConfig.INSTANCE.createDefault(configFile, executor);
		} else {
			GlobalConfig.INSTANCE.load(configFile, executor);
		}

		// apply log non-production log level, if needed
		if (GlobalConfig.INSTANCE.isLogDebugEnabled()) {
			LogManager.getLogger(Defs.LOGGER_NAME).setLevel(Level.TRACE);
			log.trace("Logging: **Non-production** TRACE mode enabled.");
		}

		log.info("Server: Name is '{}'", StringUtils.defaultIfEmpty(GlobalConfig.INSTANCE.getServerName(), "<empty>"));

		// verify configuration
		boolean zoneOK = false;
		for (String zoneId : TimeZone.getAvailableIDs()) {
			if (zoneId.equals(GlobalConfig.INSTANCE.getServerTimeZone())) {
				zoneOK = true;
				break;
			}
		}
		if (zoneOK) {
			log.info("Server: Time zone is '{}'", GlobalConfig.INSTANCE.getServerTimeZone());
		} else {
			throw new RuntimeException(GlobalConfig.INSTANCE.getServerTimeZone() + " - time zone not found!");
		}

		// try to display server version by looking into the MANIFEST.MF file
		try {
			var resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				try {
					Manifest manifest = new Manifest(resources.nextElement().openStream());
					final Attributes mainAttributes = manifest.getMainAttributes();
					final String title = StringUtils.defaultString(mainAttributes.getValue("Implementation-Title"));
					if (title.startsWith("CurrencyBG")) {
						log.info("App info: {} v{}", title,
								StringUtils.defaultString(mainAttributes.getValue("Implementation-Version")));
					}
				} catch (IOException e) {
					System.err.println("Manifest load error! " + e.getMessage());
				}
			}
		} catch (Exception t) {
			System.err.println("Could not find application version! Error: " + t.getMessage());
		}

		log.info("Server: Cleanup threshold is {} days", GlobalConfig.INSTANCE.getCleanupInterval());
		log.info("Server: GCP Project ID is '{}'", GlobalConfig.INSTANCE.getGcpProjectId());
		log.info("Server: GCP Firebase url is '{}'", GlobalConfig.INSTANCE.getGcpFirebaseUrl());

		log.info("Initializing Firebase ...");

		try (var serviceAccount = Bootstrap.class.getResourceAsStream("/currencybg-app-ad0dcf2da0f3.json")) {
			var credentials = GoogleCredentials.fromStream(
					Objects.requireNonNull(serviceAccount, "serviceAccount is <null>"));

			FirebaseApp.initializeApp(FirebaseOptions.builder().setCredentials(credentials)
					.setDatabaseUrl(GlobalConfig.INSTANCE.getGcpFirebaseUrl()).build());

			FirestoreDataSource.setCredentials(credentials);
		} catch (IOException e) {
			throw new RuntimeException("Failed initializing Firebase!", e);
		}

		if (GlobalConfig.INSTANCE.getBotToken().isBlank() && GlobalConfig.INSTANCE.getBotChannel().isBlank()) {
			log.warn("Telegram: Neither bot token, nor bot channel config found.");
		} else if (GlobalConfig.INSTANCE.getBotToken().isEmpty()) {
			log.warn("Telegram: Bot token config not set.");
		} else if (GlobalConfig.INSTANCE.getBotChannel().isEmpty()) {
			log.warn("Telegram: Bot channel config not set.");
		}

		log.info("Starting threads ...");

		executor.scheduleWithFixedDelay(new ReporterHeartbeat(), Defs.REPORTER_UPDATE_FIRST_INTERVAL,
				Defs.REPORTER_UPDATES_PERIODIC_INTERVAL, TimeUnit.SECONDS);

		executor.scheduleWithFixedDelay(new UpdateHeartbeat(vertx), Defs.UPDATE_FIRST_INTERVAL,
				Defs.UPDATES_PERIODIC_INTERVAL, TimeUnit.SECONDS);

		executor.scheduleWithFixedDelay(new CleanupHeartbeat(), Defs.CLEANUP_FIRST_INTERVAL,
				Defs.CLEANUP_PERIODIC_INTERVAL, TimeUnit.SECONDS);
	}

	private void logConsole(String message) {
		System.out.println(message);
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
