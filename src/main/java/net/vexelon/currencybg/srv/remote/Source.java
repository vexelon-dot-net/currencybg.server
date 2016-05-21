package net.vexelon.currencybg.srv.remote;

import java.util.List;
import java.util.Map;

import net.vexelon.currencybg.srv.db.models.CurrencyData;

public interface Source {

	/**
	 * Fetches exchange rates from the underlying source and serves back a ready
	 * to consume {@link CurrencyData} language mapped list.
	 * 
	 * @return {@link Map} of languages for each of which a list of
	 *         {@link CurrencyData} is available.
	 * @throws SourceException
	 */
	List<CurrencyData> downloadRates() throws SourceException;
}
