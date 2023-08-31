package net.vexelon.currencybg.srv.db.models;

import net.vexelon.currencybg.srv.remote.*;
import net.vexelon.currencybg.srv.reports.Reporter;

public enum Sources {

	BNB(1),
	FIB(100),
	TAVEX(200),
	POLANA1(300),
	FACTORIN(400),
	UNICREDIT(500),
	@Deprecated(forRemoval = true) SGEB(600),
	CRYPTO(700),
	CHANGEPARTNER(800),
	@Deprecated(forRemoval = true) FOREXHOUSE(900),
	ALLIANZ(1000),
	@Deprecated(forRemoval = true) CRYPTOBANK(1100),
	@Deprecated(forRemoval = true) BITCOINSHOUSE(1200),
	XCHANGE(1300),
	ALTCOINS(1400);

	private int id;

	Sources(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	/**
	 * @param id
	 * @return {@link Sources} or {@code null}.
	 */
	public static Sources valueOf(int id) {
		for (Sources s : Sources.values()) {
			if (s.getID() == id) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Creates a new {@link Source} mapped via the {@link Sources} constant.
	 *
	 * @param reporter
	 * @return
	 */
	public Source newInstance(Reporter reporter) {
		return switch (id) {
			// BNB
			case 1 -> new BNBSource(reporter);

			// FIB
			case 100 -> new FIBSource(reporter);

			// TAVEX
			case 200 -> new TavexSource(reporter);

			// POLANA1
			case 300 -> new Polana1(reporter);

			// FACTORIN
			case 400 -> new Factorin(reporter);

			// UNICREDIT
			case 500 -> new UnicreditSource(reporter);

			// Societe Generale Express Bank (Deprecated)
			case 600 -> new SocieteGenerale(reporter);

			// Crypto (Bitcoin Source)
			case 700 -> new CryptoBGSource(reporter);

			// Changepartner
			case 800 -> new ChangepartnerSource(reporter);

			// ForexHouse
			case 900 -> new ForexHouseSource(reporter);

			// ForexHouse (Deprecated)
			case 1000 -> new AllianzSource(reporter);

			// CryptoBank (Deprecated)
			case 1100 -> new CryptoBankSource(reporter);

			// BitcounsHouse (Deprecated)
			case 1200 -> new BitcoinsHouseSource(reporter);

			// xChangeBG
			case 1300 -> new XChangeBGSource(reporter);

			// ALTCOINS
			case 1400 -> new AltcoinsSource(reporter);

			// <unknown>
			default -> throw new RuntimeException("Invalid source id (" + id + ")!");
		};
	}
}
