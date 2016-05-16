package net.vexelon.currencybg.srv.reports;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractReporter implements Reporter {

	private static final String CLRF = System.getProperty("line.separator");

	protected String name;
	protected StringBuilder buffer = new StringBuilder();

	public AbstractReporter(String name) {
		this.name = name;
	}

	@Override
	public void write(String message) {
		buffer.append(message).append(CLRF);
	}

	@Override
	public void write(String format, String... arguments) {
		buffer.append(String.format(StringUtils.replace(format, "{}", "%s"), arguments)).append(CLRF);
	}

	@Override
	public String getName() {
		return name;
	}

}
