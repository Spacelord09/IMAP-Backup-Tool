package de.spacelord.imapbackuptool;
// Credits: With some help from Nicola Sovic aka. Finnchen123 (nicolasovic.ch)

import de.spacelord.imapbackuptool.assets.MailGrabber;

import de.spacelord.imapbackuptool.assets.*;

public class Main {

	public static int DEBUG = 1;

	public static void main(String[] args) {
		ConsoleHandler conhandler = new ConsoleHandler();
		ConfigHandler config = new ConfigHandler(conhandler);
		MailGrabber mailgrab = new MailGrabber(conhandler, config);
		mailgrab.downloadEmails(config.receiveData("protocol"), config.receiveData("host"), config.receiveData("port"), config.receiveData("userName"), config.receiveData("password"));
	}
}
