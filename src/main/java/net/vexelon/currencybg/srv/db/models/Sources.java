package net.vexelon.currencybg.srv.db.models;

import net.vexelon.currencybg.srv.remote.BNBSource;
import net.vexelon.currencybg.srv.remote.Crypto;
import net.vexelon.currencybg.srv.remote.FIBSource;
import net.vexelon.currencybg.srv.remote.Factorin;
import net.vexelon.currencybg.srv.remote.Polana1;
import net.vexelon.currencybg.srv.remote.SocieteGenerale;
import net.vexelon.currencybg.srv.remote.Source;
import net.vexelon.currencybg.srv.remote.TavexSource;
import net.vexelon.currencybg.srv.remote.UnicreditSource;
import net.vexelon.currencybg.srv.reports.Reporter;

public enum Sources {

	BNB(1),
	FIB(100),
	TAVEX(200),
	POLANA1(300),
	FACTORIN(400),
	UNICREDIT(500),
	SGEB(600),
	CRYPTO(700);

	private int id;

	Sources(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	/**
	 * 
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
		switch (id) {
		// BNB
		case 1:
			return new BNBSource(reporter);

		// FIB
		case 100:
			return new FIBSource(reporter);

		// TAVEX
		case 200:
			return new TavexSource(reporter);

		// POLANA1
		case 300:
			return new Polana1(reporter);

		// FACTORIN
		case 400:
			return new Factorin(reporter);

		// UNICREDIT
		case 500:
			return new UnicreditSource(reporter);

		// Societe Generale Express Bank
		case 600:
			return new SocieteGenerale(reporter);

		// Crypto (Bitcoin Source)
		case 700:
			return new Crypto(reporter);

		// <unknown>
		default:
			throw new RuntimeException("Invalid source id (" + id + ")!");
		}
	}
}
