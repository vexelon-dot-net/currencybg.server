package net.vexelon.currencybg.srv.remote;

import net.vexelon.currencybg.srv.db.models.CurrencyData;

import java.util.List;

public interface Source {

	/**
	 * Fetches exchange rates from the underlying source
	 *
	 * @param callback Override the {@link Callback#onCompleted(List)} to receive a
	 *                 {@link CurrencyData} list and {@link Callback#onFailed(Exception)}} for
	 *                 error notifications in case download fails.
	 */
	void getRates(final Callback callback) throws SourceException;

	/**
	 * @return Source logical name
	 */
	String getName();

	/**
	 * Currencies download callback
	 */
	interface Callback {

		/**
		 * Notifies the caller that currencies download has finished
		 */
		void onCompleted(List<CurrencyData> currencyDataList);

		/**
		 * Notifies the caller that there was a server error
		 *
		 * @param t {@link Throwable} error
		 */
		void onFailed(Throwable t);
	}
}
