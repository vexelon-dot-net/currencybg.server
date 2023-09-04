/*
 * The MIT License
 *
 * Copyright (c) 2015 Petar Petrov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.vexelon.currencybg.srv.db;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.ReportData;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the available read-write operations to and from an underlying
 * data source implementation.
 */
public interface DataSource extends Closeable {

	@Nonnull
	static DataSource newDataSource() {
		return new FirestoreDataSource();
	}

	/**
	 * Establishes connection to data source.
	 *
	 * @throws DataSourceException If an SQL error is thrown.
	 */
	void connect() throws DataSourceException;

	/**
	 * Fetches a list of currencies for the current date which are after
	 * DateTime
	 *
	 * @param timeFrom
	 * @return
	 * @throws DataSourceException
	 */
	String getAllCurrentRatesAfter(Date timeFrom) throws DataSourceException;

	/**
	 * Fetches a list of currencies for the current date which are after DateTime for a {@code source}
	 *
	 * @param timeFrom
	 */
	String getAllCurrentRatesAfter(int sourceId, Date timeFrom) throws DataSourceException;

	/**
	 * Fetches a list of currencies by date
	 *
	 * @param dateFrom
	 * @return
	 * @throws DataSourceException
	 */
	String getAllRates(Date dateFrom) throws DataSourceException;

	/**
	 * Fetches a list of currencies by sourceId and date
	 *
	 * @param sourceId
	 * @param dateFrom
	 * @return
	 * @throws DataSourceException
	 */
	String getAllRates(int sourceId, Date dateFrom) throws DataSourceException;

	/**
	 * Deletes all currency rates older than {@code ageInDays}
	 */
	void cleanupRates(int ageInDays) throws DataSourceException;

	/**
	 * Fetches a list of sources
	 *
	 * @param isActiveOnly If {@code true}, only actives sources will be fetched.
	 */
	@Nonnull
	Collection<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException;

	/**
	 * Check whether the authentication is valid
	 *
	 * @return
	 * @throws DataSourceException
	 */
	boolean isCheckAuthentication(String authenticationKey) throws DataSourceException;

	/**
	 * Adds Map of exchange rates in DB.
	 *
	 * @param rates A {@link Map} of language and {@link CurrencyData} list
	 *              values.
	 * @throws DataSourceException
	 */
	void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException;

	/**
	 * Adds rates from a source
	 *
	 * @param rates A list of {@link CurrencyData} fetched rates.
	 * @throws DataSourceException
	 */
	void addRates(Collection<CurrencyData> rates) throws DataSourceException;

	/**
	 * Update some or all fields in cbg_sources table
	 *
	 * @param sourceId
	 * @param source
	 * @throws DataSourceException
	 */
	void updateSource(int sourceId, CurrencySource source) throws DataSourceException;

	/**
	 * Writes an error message report in the database
	 *
	 * @param message Formatted message.
	 */
	void addReportMessage(String message) throws DataSourceException;

	/**
	 * @return A list of all reports waiting to be sent.
	 */
	@Nonnull
	Collection<ReportData> getReports() throws DataSourceException;

	/**
	 * Remove all reports specified
	 */
	void deleteReports(@Nonnull Collection<ReportData> reports) throws DataSourceException;
}
