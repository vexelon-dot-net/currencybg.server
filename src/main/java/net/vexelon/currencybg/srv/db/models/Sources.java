package net.vexelon.currencybg.srv.db.models;

import io.vertx.core.Vertx;
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
	 * Creates a new {@link Source} mapped via the {@link Sources} constant
	 */
	public Source newInstance(Vertx vertx, Reporter reporter) {
		return switch (id) {
			// BNB
			case 1 -> new BNBSource(vertx, reporter);

			// FIB
			case 100 -> new FIBSource(vertx, reporter);

			// TAVEX
			case 200 -> new TavexSource(vertx, reporter);

			// POLANA1
			case 300 -> new Polana1(vertx, reporter);

			// FACTORIN
			case 400 -> new Factorin(vertx, reporter);

			// UNICREDIT
			case 500 -> new UnicreditSource(vertx, reporter);

			// Societe Generale Express Bank (Deprecated)
			case 600 -> throw new IllegalArgumentException("SocieteGenerale is no long supported!");

			// Crypto (Bitcoin Source)
			case 700 -> new CryptoBGSource(vertx, reporter);

			// Changepartner
			case 800 -> new ChangepartnerSource(vertx, reporter);

			// ForexHouse
			case 900 -> throw new IllegalArgumentException("ForexHouse is no long supported!");

			// ForexHouse (Deprecated)
			case 1000 -> new AllianzSource(vertx, reporter);

			// CryptoBank (Deprecated)
			case 1100 -> throw new IllegalArgumentException("CryptoBank is no long supported!");

			// BitcounsHouse (Deprecated)
			case 1200 -> throw new IllegalArgumentException("BitcoinsHouse is no long supported!");

			// xChangeBG
			case 1300 -> new XChangeBGSource(vertx, reporter);

			// ALTCOINS
			case 1400 -> new AltcoinsSource(vertx, reporter);

			// <unknown>
			default -> throw new RuntimeException("Invalid source id (" + id + ")!");
		};
	}
}
