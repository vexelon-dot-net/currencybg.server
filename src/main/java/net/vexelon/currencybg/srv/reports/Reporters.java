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
		for (var r : values()) {
			if (r.getId().equals(name)) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Creates a new {@link Reporter} mapped via the {@link Reporters} constant.
	 */
	public Reporter newInstance() {
		return switch (id) {
			case "sparkpost" -> new SparkPostReporter();
			case "telegram" -> new TelegramReporter();
			default -> throw new RuntimeException("Invalid reporter id (" + id + ")");
		};
	}
}
