package net.vexelon.currencybg.srv.db.models;

public enum Sources {

	BNB(1), FIB(100), Tavex(200);

	private int id;

	Sources(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}
}
