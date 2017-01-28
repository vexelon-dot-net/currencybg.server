package net.vexelon.currencybg.srv.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sparkpost.Client;
import com.sparkpost.exception.SparkPostException;

import net.vexelon.currencybg.srv.GlobalConfig;

public class SparkPostReporter extends AbstractReporter {

	private static final Logger log = LoggerFactory.getLogger(SparkPostReporter.class);

	public SparkPostReporter(String name) {
		super(name);
	}

	public SparkPostReporter() {
		this("SparkPost");
	}

	@Override
	public void send() throws IOException {

		// Set the recipients' email
		List<String> emails = Arrays.asList(GlobalConfig.INSTANCE.getSparkPostEmails().split("\\s*,\\s*"));

		if (!GlobalConfig.INSTANCE.getSparkPostAPIKey().isEmpty() && emails.size() > 0) {

			// String API_KEY = GlobalConfig.INSTANCE.getSparkPostAPIKey();
			Client client = new Client(GlobalConfig.INSTANCE.getSparkPostAPIKey());

			try {
				client.sendMessage("noreply-currencybg@vexelon.net", emails, "CurrencyBG Report", buffer.toString(),
						null);
			} catch (SparkPostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
