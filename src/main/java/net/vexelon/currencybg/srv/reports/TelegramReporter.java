package net.vexelon.currencybg.srv.reports;

import java.io.IOException;

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

		TelegramBot bot = TelegramBotAdapter
				.build(/* "264497873:AAGjShbt3-CJkuSHJLwyMccTbvh-0PxNZ68" */GlobalConfig.INSTANCE.getBotToken());

		bot.execute(new SendDocument(/* "@ceco88" */GlobalConfig.INSTANCE.getChannel(),
				buffer.toString().getBytes("UTF-8")), new Callback<SendDocument, SendResponse>() {
					@Override
					public void onFailure(SendDocument request, IOException e) {
						log.error("Fail Telegram SendDocument: ", e);
					};

					@Override
					public void onResponse(SendDocument request, SendResponse response) {

					};
				});

		// System.out.println("Telegram: " + buffer.toString());
	}

}
