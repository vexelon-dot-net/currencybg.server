package net.vexelon.currencybg.srv.reports;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.response.SendResponse;

import net.vexelon.currencybg.srv.GlobalConfig;

public class TelegramReporter extends AbstractReporter {

	private static final Logger log = LoggerFactory.getLogger(TelegramReporter.class);

	public TelegramReporter(String name) {
		super(name);
	}

	public TelegramReporter() {
		this("Telegram");
	}

	@Override
	public void send() throws IOException {
		if (!GlobalConfig.INSTANCE.getBotToken().isEmpty() && !GlobalConfig.INSTANCE.getBotChannel().isEmpty()) {

			TelegramBot bot = TelegramBotAdapter.build(GlobalConfig.INSTANCE.getBotToken());

			bot.execute(
					new SendDocument(GlobalConfig.INSTANCE.getBotChannel(),
							buffer.toString().getBytes(StandardCharsets.UTF_8.name())),
					new Callback<SendDocument, SendResponse>() {
						@Override
						public void onFailure(SendDocument request, IOException e) {
							log.error("Telegram SendDocument failed!", e);
						};

						@Override
						public void onResponse(SendDocument request, SendResponse response) {
							// do nothing
						};
					});
		}
	}

}
