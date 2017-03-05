package net.vexelon.currencybg.srv.reports;

public enum Reporters {

	SPARKPOST("SPARKPOST"),
	TELEGRAM("TELEGRAM");

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

	/**
	 * Creates a new {@link Reporter} mapped via the {@link Reporters} constant.
	 *
	 * @param reporter
	 * @return
	 */
	public Reporter newInstance() {
		switch (id) {
		case "SPARKPOST":
			return new SparkPostReporter();
		case "TELEGRAM":
			return new TelegramReporter();
		// <unknown>
		default:
			throw new RuntimeException("Invalid report id (" + id + ")!");
		}
	}

}
