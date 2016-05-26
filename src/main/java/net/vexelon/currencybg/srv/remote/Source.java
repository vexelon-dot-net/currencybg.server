package net.vexelon.currencybg.srv.remote;

import java.util.List;

import net.vexelon.currencybg.srv.db.models.CurrencyData;

public interface Source {

	/**
	 * Fetches exchange rates from the underlying source.
	 * 
	 * @param callback
	 *            Override the {@link Callback#onCompleted(List)} to receive a
	 *            {@link CurrencyData} list and {@link Callback#onFailed()} for
	 *            error notifications in case download fails.
	 * @throws SourceException
	 */
	void getRates(final Callback callback) throws SourceException;

	/**
	 * Currencies download callback
	 * 
	 */
	public interface Callback {

		/**
		 * Notifies the caller that currencies download has finished.
		 * 
		 * @param currencyDataList
		 */
		void onCompleted(List<CurrencyData> currencyDataList);

		/**
		 * Notifies the caller that there was a server error.
		 * 
		 */
		void onFailed();

	}
}
