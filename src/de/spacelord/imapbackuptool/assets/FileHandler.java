package de.spacelord.imapbackuptool.assets;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;

public class FileHandler {

	private ConsoleHandler conhandler;
	private ConfigHandler config;
	private Calendar calendar;
	private String mailPath;
	private String mailFilePath;

	private String prefix;

	public FileHandler(ConsoleHandler conhandler, ConfigHandler config) {
		this.conhandler = conhandler;
		this.config = config;
		this.calendar = new GregorianCalendar();
	}

	public boolean saveMail(Address[] fromAddress, String from, String subject, String tags, String toList, String ccList, Date sentDate, String messageContent, Message msg, String contentType) {
		conhandler.debug("This Method is saving mails to the filesystem ;)" + conhandler.getTimestamp("HH:mm:ss"));
		generatePath(sentDate, tags);
		
		new File(mailPath).mkdirs();
		
		// write mail text to file:
		writeMailText(from, toList, ccList, tags, subject, sentDate, messageContent);
		
		// Get attachments
		try {
			getAttachments(msg, contentType, mailPath);
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	
	// Generate path
	public String generatePath(Date sentDate, String tags) {
		//calendar.setTime(new GregorianCalendar(2020, Calendar.JANUARY, 11).getTime());
		calendar.setTime(sentDate);
		
		// Format date object into useful variables.
		String year = new DecimalFormat("0000").format(calendar.get(Calendar.YEAR));
		String month = new DecimalFormat("00").format(calendar.get(Calendar.MONTH) + 1);
		String day = new DecimalFormat("00").format(calendar.get(Calendar.DAY_OF_MONTH));
		String hour = new DecimalFormat("00").format(calendar.get(Calendar.HOUR_OF_DAY));
		String minute = new DecimalFormat("00").format(calendar.get(Calendar.MINUTE));
		String second = new DecimalFormat("00").format(calendar.get(Calendar.SECOND));
		conhandler.debug("Sentdate=" + sentDate);
		
		// Generate path structure.
		StringBuilder path = new StringBuilder();
		path.append(year + File.separator);
		path.append(month + File.separator);
		path.append(day + File.separator);
		path.append(hour + minute + second + File.separator);
		//path.append("" + File.separator);
		
		// Print generated path to debug and return it as string.
		conhandler.debug("Generated Path> " + prefix + path.toString());
		
		prefix = config.receiveData("path_prefix");
		
		if(config.receiveData("tagsAsFileName").toLowerCase().contains("true")) {
			mailFilePath = prefix + path.toString() + tags + ".txt";
		} else {
			mailFilePath = prefix + path.toString() + config.receiveData("mailFile");			
		}
		mailPath = prefix + path.toString();
		
		return mailPath;
	}

	File file;
	private void getAttachments(Message msg, String contentType, String path) throws IOException, MessagingException {
		if (contentType.contains("multipart")) {
			// content may contain attachments
			Multipart multiPart = (Multipart) msg.getContent();
			int numberOfParts = multiPart.getCount();
			for (int partCount = 0; partCount < numberOfParts; partCount++) {
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
				if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
					// this part is attachment
					BodyPart bodyPart = multiPart.getBodyPart(partCount);
		
					// generate file object for attachment
					File file = new File(mailPath + bodyPart.getFileName());
					// copy data to file object
					FileUtils.copyInputStreamToFile(bodyPart.getInputStream(), file);
				}
			}
		}
	}

	private void writeMailText(String from, String toList, String ccList, String tags, String subject, Date sentDate, String messageContent) {
		StringBuilder builder = new StringBuilder();
		conhandler.debug(mailFilePath);
		File MailText = new File(mailFilePath);
		builder.append("From: " + from + System.lineSeparator());
		builder.append("To: " + toList + System.lineSeparator());
		builder.append("CC: " + ccList + System.lineSeparator());
		// If the tags are not used as filename print to file.
		if(config.receiveData("tagsAsFileName").toLowerCase().contains("false")) {
			builder.append("Tags:" + tags + System.lineSeparator());
		}
		builder.append("Subject: " + subject + System.lineSeparator());
		builder.append("Sent Date: " + sentDate + System.lineSeparator());
		builder.append("Message: " + messageContent + System.lineSeparator());
		try {
			FileUtils.writeStringToFile(MailText, builder.toString(), "UTF8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}

