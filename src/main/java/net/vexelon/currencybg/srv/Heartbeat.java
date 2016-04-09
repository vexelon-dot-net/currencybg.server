package net.vexelon.currencybg.srv;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencyLocales;
import net.vexelon.currencybg.srv.remote.BNBSource;
import net.vexelon.currencybg.srv.remote.Source;
import net.vexelon.currencybg.srv.remote.SourceException;

/**
 * Fetches currencies from remote server and imports them into the database.
 *
 */
public class Heartbeat implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Heartbeat.class);

	@Override
	public void run() {
		log.info("Updating rates from remote server ...");
		Source source = new BNBSource();
		try {
			Map<CurrencyLocales, List<CurrencyData>> downloadRates = source.downloadRates();
			log.debug("Importing downloaded rates in database ...");
			// TODO insert in db
			for (Map.Entry<CurrencyLocales, List<CurrencyData>> rates : downloadRates.entrySet()) {
				log.trace("*** downloaded locale: {}", rates.getKey());
				for (CurrencyData currency : rates.getValue()) {
					log.trace("Currency: {} ({}) = {}", currency.getName(), currency.getCode(), currency.getRate());
				}
			}
		} catch (SourceException e) {
			log.error("Could not download currencies from remote!", e);
		}
	}
}
