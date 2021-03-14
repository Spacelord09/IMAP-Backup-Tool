package de.spacelord.imapbackuptool.assets;

import de.spacelord.imapbackuptool.Main;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ConsoleHandler {

	private Timestamp timestamp;
	private SimpleDateFormat sdf;

	public ConsoleHandler() {
//		this.language = new LanguageHandler(this);
	}

	// Output to Console
	public void outputToConsole(Object output) {
		System.out.println(output);
	}

	public void debug(Object output) {
		if (Main.DEBUG == 1) {
			outputToConsole("[" + getTimestamp("HH:mm:ss") + "] [DEBUG]> " + output);
		}
	}

	public String getTimestamp(String format) {
		sdf = new SimpleDateFormat(format);
		timestamp = new Timestamp(System.currentTimeMillis());
		return sdf.format(timestamp);
	}

	public void exit(Integer exitcode, Object debug_message) {
		debug("Exitting due to: " + debug_message);
		System.exit(exitcode);
	}

	// Generic sleep method.
	public void sleep(Integer sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}
}
