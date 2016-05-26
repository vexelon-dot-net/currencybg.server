package net.vexelon.currencybg.srv.db.models;

import net.vexelon.currencybg.srv.remote.BNBSource;
import net.vexelon.currencybg.srv.remote.Source;
import net.vexelon.currencybg.srv.remote.TavexSource;
import net.vexelon.currencybg.srv.reports.Reporter;

public enum Sources {

	BNB(1), FIB(100), TAVEX(200);

	private int id;

	Sources(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public Source newInstance(Reporter reporter) {
		switch (id) {
		// BNB
		case 1:
			return new BNBSource();
		// TAVEX
		case 200:
			return new TavexSource(reporter);
		default:
			throw new RuntimeException("Invalid source id (" + id + ")!");
		}
	}
}
