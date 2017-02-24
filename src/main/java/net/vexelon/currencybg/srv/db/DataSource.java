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

import java.io.Closeable;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.ReportData;

/**
 * Encapsulates the available read-write operations to and from an underlying
 * data source implementation.
 */
public interface DataSource extends Closeable {

	/**
	 * Establishes connection to data source.
	 * 
	 * @param context
	 * @throws DataSourceException
	 *             If an SQL error is thrown.
	 */
	Connection connect() throws DataSourceException;

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
	 * Fetches a list of currencies for the current date which are after
	 * DateTime by sourceId
	 * 
	 * @param sourceId
	 * @param timeFrom
	 * @return
	 * @throws DataSourceException
	 */
	String getAllCurrentRatesAfter(Integer sourceId, Date timeFrom) throws DataSourceException;

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
	String getAllRates(Integer sourceId, Date dateFrom) throws DataSourceException;

	/**
	 * Fetches a list of sources in DB by id
	 * 
	 * @param id
	 * @return
	 * @throws DataSourceException
	 */
	CurrencySource getSourceById(int id) throws DataSourceException;

	/**
	 * Fetches a list of sources in DB
	 * 
	 * @return
	 * @throws DataSourceException
	 */

	List<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException;

	/**
	 * Check whether the authentication is valid
	 * 
	 * @param headerName
	 * @param headerValue
	 * @return
	 * @throws DataSourceException
	 */
	boolean isCheckAuthentication(String authenticationKey) throws DataSourceException;

	/**
	 * Adds Map of exchange rates in DB.
	 * 
	 * @param rates
	 *            A {@link Map} of language and {@link CurrencyData} list
	 *            values.
	 * @throws DataSourceException
	 */
	void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException;

	/**
	 * Add rates from by one source
	 * 
	 * @param sourceId
	 * @param rates
	 * @throws DataSourceException
	 */
	void addRates(List<CurrencyData> rates) throws DataSourceException;

	/**
	 * Update some or all fields in cbg_sources table
	 * 
	 * @param sourceId
	 * @param source
	 * @throws DataSourceException
	 */
	void updateSource(int sourceId, CurrencySource source) throws DataSourceException;

	/**
	 * Write an error message in DB
	 * 
	 * @param source
	 *            throws an error
	 * @param message
	 * @throws DataSourceException
	 */
	void addReport(String message) throws DataSourceException;

	/**
	 * Return information for all reports in DB
	 * 
	 * @return
	 * @throws DataSourceException
	 */
	List<ReportData> getReports() throws DataSourceException;

	/**
	 * Clear all information for the reports in DB
	 * 
	 * @throws DataSourceException
	 */
	void deleteReports() throws DataSourceException;

}
