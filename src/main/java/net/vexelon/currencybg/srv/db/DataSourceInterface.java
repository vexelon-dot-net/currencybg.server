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

/**
 * Encapsulates the available read-write operations to and from an underlying
 * data source implementation.
 */
public interface DataSourceInterface extends Closeable {

	/**
	 * Establishes connection to data source.
	 * 
	 * @param context
	 * @throws DataSourceException
	 *             If an SQL error is thrown.
	 */
	Connection connect() throws DataSourceException;

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
	 * Check whether the authentication is valid
	 * 
	 * @param headerName
	 * @param headerValue
	 * @return
	 * @throws DataSourceException
	 */
	boolean isCheckAuthentication(String authenticationKey) throws DataSourceException;

	/**
	 * Adds exchange rates in DB.
	 * 
	 * @param rates
	 *            A {@link Map} of language and {@link CurrencyData} list
	 *            values.
	 * @throws DataSourceException
	 */
	void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException;

}
