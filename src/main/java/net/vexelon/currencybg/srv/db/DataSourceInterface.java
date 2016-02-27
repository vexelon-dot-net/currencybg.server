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
import java.util.List;

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
	Connection dbConnect() throws DataSourceException;

	// TODO - test interface
	String selectRecordsFromTable() throws DataSourceException;

	/**
	 * Fetches a list of dates for which exchange rates were downloaded and
	 * available in the underlying data source.
	 * 
	 * @param locale
	 * @return A {@link List} of {@link Date} objects or an empty {@link List},
	 *         if no dates are available.
	 * @throws DataSourceException
	 */
	// List<Date> getAvailableRatesDates(/* CurrencyLocales */String locale)
	// throws DataSourceException;

	/**
	 * Fetches all exchange rates, for all dates, from the underlying data
	 * source.
	 * 
	 * @param locale
	 * @return {@link List} or {@code null}, if no rates are
	 *         available.
	 * @throws DataSourceException
	 */
	// TODO - temp return type
	/* List<CurrencyData> */String getRates(/* CurrencyLocales */String locale) throws DataSourceException;

	/**
	 * Fetches exchange rates for a given date.
	 * 
	 * @param locale
	 * @param date
	 * @return {@link List} or {@code null}, if no rates are
	 *         available for the given date.
	 * @throws DataSourceException
	 */
	// List<CurrencyData> getRates(/* CurrencyLocales */String locale, Date
	// date) throws DataSourceException;

	// List<CurrencyData> getFixedRates(/* CurrencyLocales */String locale, Date
	// date) throws DataSourceException;

	/**
	 * Adds exchange rates data for given download {@link Date}.
	 * 
	 * @param rates
	 *            A {@link Map} of language and {@link CurrencyData} list
	 *            values.
	 * @throws DataSourceException
	 */
	// void addRates(Map</* CurrencyLocales */String, List<CurrencyData>> rates)
	// throws DataSourceException;

	/**
	 * Fetches the latest exchange rates from the underlying data source.
	 * 
	 * @param locale
	 * @throws DataSourceException
	 */
	// List<CurrencyData> getLastRates(/* CurrencyLocales */String locale)
	// throws DataSourceException;

	/**
	 * Fetches the last exchange fixed rates from the underlying data source.
	 *
	 * @param locale
	 * @return
	 * @throws DataSourceException
	 */
	// List<CurrencyData> getLastFixedRates(/* CurrencyLocales */String locale)
	// throws DataSourceException;
}
