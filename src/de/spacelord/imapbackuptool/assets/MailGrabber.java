package de.spacelord.imapbackuptool.assets;

import java.util.Properties;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class MailGrabber {

	private ConsoleHandler conhandler;
	private ConfigHandler config;
	private Integer mailid;

	// constructor to get config and conhandler.
	public MailGrabber(ConsoleHandler conhandler, ConfigHandler config) {
		this.conhandler = conhandler;
		this.config = config;
	}

	/**
	 * Returns a Properties object which is configured for a POP3/IMAP server
	 */

	// TODO
	private Properties getServerProperties(String protocol, String host, String port) {
		Properties properties = new Properties();

		// server setting
		properties.put(String.format("mail.%s.host", protocol), host);
		properties.put(String.format("mail.%s.port", protocol), port);

		// SSL setting
		properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
		properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

		return properties;
	}

	/**
	 * Downloads new messages and fetches details for each message.
	 */
	public void downloadEmails(String protocol, String host, String port, String userName, String password) {
		Properties properties = getServerProperties(protocol, host, port);
		Session session = Session.getDefaultInstance(properties);

		try {
			// connects to the message store
			Store store = session.getStore(protocol);
			store.connect(userName, password);
			// opens the source folder
			Folder sourcefolder = store.getFolder(config.receiveData("mail_source_folder"));
			sourcefolder.open(Folder.READ_WRITE);
			// fetches messages from server
			Message[] messages = sourcefolder.getMessages();

			// Variables for messages
			Mail mail;
			FileHandler filehandler = new FileHandler(conhandler, config);
			for (int i = 0; i < messages.length; i++) {
				mailid = i + 1;
				mail = new Mail(messages[i], mailid, conhandler);
				if (mail.isValid()) {

					if (mail.saveMail(filehandler)) {
						conhandler.debug("Save to fs successful");

//						if (moveMail(config.receiveData("mail_destination_folder"), sourcefolder)) {
//							conhandler.debug("Mail sucessfully moved to done dir on server.");
//						} else {
//							conhandler.debug("error at moving mail:" + mailid);
//						}
					} else {
						conhandler.debug("ERROR: An Error message should be visible over this message. If not something went teribly wrong!");
					}
				} else {
					conhandler.debug("Mail not valid!");
				}
				// Print current mail to console
				conhandler.debug(mail.toString());
			}

			// disconnect
			sourcefolder.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			conhandler.exit(1, "No provider for protocol: " + protocol);
			ex.printStackTrace();
		} catch (MessagingException ex) {
			conhandler.exit(1, "Could not connect to the message store");
			ex.printStackTrace();
		}

	}

}
