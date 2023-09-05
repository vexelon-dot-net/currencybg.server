package net.vexelon.currencybg.srv;

import net.vexelon.currencybg.srv.db.DataSource;
import net.vexelon.currencybg.srv.db.DataSourceException;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.SourceUpdateRestrictions;
import net.vexelon.currencybg.srv.db.models.Sources;
import net.vexelon.currencybg.srv.remote.Source;
import net.vexelon.currencybg.srv.remote.SourceException;
import net.vexelon.currencybg.srv.reports.DataSourceReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Fetches currencies from remote server and imports them into the database
 */
public class UpdateHeartbeat implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(UpdateHeartbeat.class);

	private boolean isUpdateGo(SourceUpdateRestrictions updateRestrictions) {
		if (!updateRestrictions.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("Source Update Restrictions: {}", updateRestrictions);
			}

			try {
				/*
				 * Hour and minute in database are always relevant to the
				 * Europe/Sofia time zone
				 */
				var dateTimeSofia = ZonedDateTime.now(ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA));
				if (log.isTraceEnabled()) {
					log.trace("*** Time in {} is {}", Defs.DATETIME_TIMEZONE_SOFIA, dateTimeSofia.toString());
				}

				if (dateTimeSofia.getDayOfWeek() == DayOfWeek.SATURDAY
						|| dateTimeSofia.getDayOfWeek() == DayOfWeek.SUNDAY) {
					/*
					 * Weekends
					 */
					if (!updateRestrictions.isEnabledOnWeekends()) {
						return false;
					} else if (dateTimeSofia.getDayOfWeek() == DayOfWeek.SUNDAY
							&& !updateRestrictions.isEnabledOnSunday()) {
						return false;
					}

					var notBefore = ZonedDateTime.of(dateTimeSofia.toLocalDate(),
							LocalTime.parse(updateRestrictions.getWeekendsNotBefore()),
							ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA));

					var notAfter = ZonedDateTime.of(dateTimeSofia.toLocalDate(),
							LocalTime.parse(updateRestrictions.getWeekendsNotAfter()),
							ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA));

					if (log.isTraceEnabled()) {
						log.trace("[Weekend] Note before - {}", notBefore.toString());
						log.trace("[Weekend] Note after - {}", notAfter.toString());
					}

					return dateTimeSofia.isAfter(notBefore) && dateTimeSofia.isBefore(notAfter);
				} else {
					/*
					 * Week days
					 */
					var notBefore = ZonedDateTime.of(dateTimeSofia.toLocalDate(),
							LocalTime.parse(updateRestrictions.getWeekdaysNotBefore()),
							ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA));

					var notAfter = ZonedDateTime.of(dateTimeSofia.toLocalDate(),
							LocalTime.parse(updateRestrictions.getWeekdaysNotAfter()),
							ZoneId.of(Defs.DATETIME_TIMEZONE_SOFIA));

					if (log.isTraceEnabled()) {
						log.trace("[Weekday] Note before - {}", notBefore.toString());
						log.trace("[Weekday] Note after - {}", notAfter.toString());
					}

					return dateTimeSofia.isAfter(notBefore) && dateTimeSofia.isBefore(notAfter);
				}
			} catch (DateTimeException e) {
				log.warn("Incorrect update restrictions format or date time error!", e);
				// do not update, if time restrictions could not be parsed!
				return false;
			}
		}

		// all OK!
		return true;
	}

	@Override
	public void run() {
		log.trace("Downloading rates from sources ...");

		try {
			try (final var dataSource = DataSource.newDataSource()) {
				/*
				 * Fetch all (active) sources from database
				 */
				dataSource.connect();
				var allSources = dataSource.getAllSources(true);

				/*
				 * Fetch currencies for every active source
				 */
				var nowCalendar = Calendar.getInstance();
				for (var currencySource : allSources) {

					// checks if it is time to update this source entry
					var sourceCalendar = Calendar.getInstance();
					sourceCalendar.setTimeInMillis(currencySource.getLastUpdate().getTime() + TimeUnit.SECONDS.toMillis(
							currencySource.getUpdatePeriod()));
					if (sourceCalendar.after(nowCalendar)) {
						if (log.isDebugEnabled()) {
							log.debug("Source {} ({}) update skipped.",
									Objects.requireNonNull(Sources.valueOf(currencySource.getSourceId()),
											"source id=" + currencySource.getSourceId() + " not found").name(),
									currencySource.getSourceId());
						}
						continue;
					}

					// check if update is allowed on this date
					var updateRestrictions = currencySource.getUpdateRestrictions();
					if (!isUpdateGo(updateRestrictions)) {
						if (log.isDebugEnabled()) {
							log.debug("Source {} ({}) updates are disabled for the current time/date.",
									Objects.requireNonNull(Sources.valueOf(currencySource.getSourceId()),
											"source id=" + currencySource.getSourceId() + " not found").name(),
									currencySource.getSourceId());
						}
						continue;
					}

					final var sourceType = Sources.valueOf(currencySource.getSourceId());
					if (sourceType != null) {
						try {
							final var reporter = new DataSourceReporter();
							final var source = sourceType.newInstance(reporter);

							// set update datetime flag
							currencySource.setLastUpdate(new Date());
							dataSource.updateSource(currencySource.getSourceId(), currencySource);

							source.getRates(new Source.Callback() {

								@Override
								public void onFailed(Exception e) {
									log.error("{} - source download failed!", source.getName(), e);

									if (!reporter.isEmpty()) {
										try {
											reporter.send();
										} catch (IOException ioe) {
											log.error("{} - Failed sending report!", source.getName(), ioe);
										}
									}
								}

								@Override
								public void onCompleted(List<CurrencyData> currencyDataList) {
									log.info("{} - source download successful.", source.getName());

									//									if (log.isTraceEnabled()) {
									//										for (var currency : currencyDataList) {
									//											log.trace(currency.toString());
									//										}
									//									}

									log.debug("{} - importing downloaded rates in database ...", source.getName());
									try (final var dataSource = DataSource.newDataSource()) {
										dataSource.connect();
										dataSource.addRates(currencyDataList);
									} catch (IOException | DataSourceException e) {
										log.error("Could not connect to database!", e);
									}

									if (!reporter.isEmpty()) {
										try {
											reporter.send();
										} catch (IOException ioe) {
											log.error("{} - Failed sending report!", source.getName(), ioe);
										}
									}
								}
							});
						} catch (SourceException e) {
							log.error("Failed fetching rates for source id='{}'!", currencySource.getSourceId(), e);
						}
					}
				}

			} catch (IOException | DataSourceException e) {
				log.error("Could not connect to database!", e);
			}
		} catch (Throwable t) {
			/*
			 * The executor swallows exceptions, so catch Throwable instead.
			 *
			 * @see http://stackoverflow.com/a/24902026
			 */
			log.error("Fatal error!", t);
		}
	}
}
