package net.vexelon.currencybg.srv;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

/**
 * Fetches currencies from remote server.
 *
 */
public class Heartbeat implements Runnable {

	@Override
	public void run() {
		System.out.println("--- heartbeat ---");

		List<String> result = Lists.newArrayList();
		for (int i = 0; i < 5; i++) {
			result.add(Integer.toString(new Random().nextInt(10)));
		}

		// TODO fetch currencies from remote server and save to db
	}
}
