package de.spacelord.imapbackuptool.assets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// 
import nu.studer.java.util.OrderedProperties;
import nu.studer.java.util.OrderedProperties.OrderedPropertiesBuilder;


public class ConfigHandler {

	private String confpath = "imapbackuptool.properties";

	ConsoleHandler conhandler;
	OrderedProperties configfile;
	OrderedPropertiesBuilder configBuilder;
	File config;

	public ConfigHandler(ConsoleHandler conhandler) {
		this.conhandler = conhandler;
		main();
	}

	private void main() {
		check_configfile();
		configfile = new OrderedProperties();
		config = new File(confpath);
		try {
			configfile.load(new FileInputStream(config));
		} catch (IOException e) {
			conhandler.debug("config File not existing or not readable.");
			e.printStackTrace();
		}
	}

	private void check_configfile() {
		// Check if all config-files are existing.
		File file = new File(confpath);
		if (file.exists()) {
			conhandler.debug("Config file exist!");
			conhandler.debug(file.getAbsolutePath());
		} else {
			conhandler.debug("configFile: " + confpath + " not existing. Creating file...");
			try {
				file.createNewFile();
				insertStartData(file);
				conhandler.debug("Creation successfull! Exiting...");
			} catch (IOException e) {
				conhandler.debug("Creation failed! Exiting...");
			}
			conhandler.exit(1, "config needs editing!");
		}
	}

	private void insertStartData(File file) {
		configBuilder = new OrderedPropertiesBuilder();
		configBuilder.withSuppressDateInComment(true);
		configfile = configBuilder.build();
		try {
			configfile.load(new FileInputStream(file));
			configfile.setProperty("protocol", "imaps");
			configfile.setProperty("host", "example.org");
			configfile.setProperty("port", "993");
			configfile.setProperty("userName", "max@example.org");
			configfile.setProperty("password", "SecretPassword");
			configfile.setProperty("mail_source_folder", "INBOX");
			//configfile.setProperty("mail_destination_folder", "DONE");	// TODO Implement
			//configfile.setProperty("move_mail", "true");	// TODO Implement
			//configfile.setProperty("allowMailfrom", "max@example.com");	// TODO Implement
			//configfile.setProperty("markAsRead", "true");	// TODO Implement
			configfile.setProperty("path_prefix", "./");
			configfile.setProperty("mailFile", "mail.txt");
			configfile.setProperty("tagsAsFileName", "false");
			configfile.store(new FileOutputStream(file), generateHelpString());
		} catch (IOException e1) {
			conhandler.debug("Config File not existing or not readable.");
		}
	}

	private String generateHelpString() {
		StringBuilder builder = new StringBuilder();
		builder.append("protocol: Valid options are pop3/imap/imaps" + System.lineSeparator());
		builder.append("host: Mailserver" + System.lineSeparator());
		builder.append("port: Mailserver port" + System.lineSeparator());
		builder.append("userName: Username" + System.lineSeparator());
		builder.append("password: Password" + System.lineSeparator());
		builder.append("mail_source_folder: The folder you want to backup" + System.lineSeparator());
		//builder.append("mail_destination_folder: TODO" + System.lineSeparator());	// TODO Implement
		//builder.append("move_mail: TODO" + System.lineSeparator());	// TODO Implement
		//builder.append("allowMailFrom: TODO" + System.lineSeparator());	// TODO Implement
		//builder.append("markAsRead: TODO" + System.lineSeparator());	// TODO Implement
		builder.append("path_prefix: Folder structure prefix" + System.lineSeparator());
		builder.append("mailFile: Name of the file that contains the mail's content" + System.lineSeparator());
		builder.append("tagsAsFileName: Use tags from subject as file name? (Add tags with # or * in subject)" + System.lineSeparator());
		builder.append(System.lineSeparator());
		return builder.toString();
	}
	
	public String receiveData(String key) {
		System.out.println("CONFIG-KEY: GET " + "'" + key + "'");
		String result = "";
		configfile = new OrderedProperties();
		try {
			configfile.load(new FileInputStream(config));
		} catch (Exception e) {
			conhandler.debug("Config File not existing or not readable.");
			e.printStackTrace();
		}
		result = configfile.getProperty(key);
		if(result == null || result.length() == 0) {
			conhandler.exit(1, "Not all attributes found. Please check config file! If necessary, you can delete the file, then a new one will be created.");
		}
		return result;
	}

}
