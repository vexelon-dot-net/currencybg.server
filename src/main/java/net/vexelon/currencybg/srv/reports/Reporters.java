package net.vexelon.currencybg.srv.reports;

public enum Reporters {

	SPARKPOST("sparkpost"),
	TELEGRAM("telegram");

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private Reporters(String id) {
		this.id = id;
	}

	public static Reporters getByName(String name) {
		for (Reporters r : values()) {
			if (r.getId().equals(name)) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Creates a new {@link Reporter} mapped via the {@link Reporters} constant.
	 *
	 * @param reporter
	 * @return
	 */
	public Reporter newInstance() {
		switch (id) {
		case "sparkpost":
			return new SparkPostReporter();
		case "telegram":
			return new TelegramReporter();
		// <unknown>
		default:
			throw new RuntimeException("Invalid report id (" + id + ")!");
		}
	}

}
