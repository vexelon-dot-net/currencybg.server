package net.vexelon.currencybg.srv.reports;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractReporter implements Reporter {

	private static final String CLRF = System.getProperty("line.separator");

	protected String name;
	protected StringBuilder buffer = new StringBuilder();

	public AbstractReporter(String name) {
		this.name = "[" + name + "] ";
	}

	protected String formatTag(String tag) {
		return "[" + tag + "] ";
	}

	@Override
	public void write(String tag, String message) {
		buffer.append(formatTag(tag)).append(message).append(CLRF);
	}

	@Override
	public void write(String tag, String format, String... arguments) {
		buffer.append(formatTag(tag))
				.append(String.format(StringUtils.replace(format, "{}", "%s"), (Object[]) arguments)).append(CLRF);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEmpty() {
		return buffer.length() == 0;
	}

}
