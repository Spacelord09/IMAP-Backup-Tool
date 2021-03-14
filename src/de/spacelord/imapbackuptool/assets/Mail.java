package de.spacelord.imapbackuptool.assets;

import java.io.IOException;
import java.util.Date;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.MimeMultipart;

public class Mail {
	private ConsoleHandler conhandler;
	//Message Object.
	private Message msg;
	private Address[] fromAddress;
	private String from;
	private String subject;
	private String tags;
	private String toList;
	private String ccList;
	private Date sentDate;
	private String messageContent;
	private String contentType;
	private Integer mailid;
	private boolean validMail = false;
	

	public Mail(Message msg, Integer mailid, ConsoleHandler conhandler) {
		this.conhandler = conhandler;
		try {
			this.msg = msg;
			this.fromAddress = msg.getFrom();
			this.from = fromAddress[0].toString();
			this.subject = msg.getSubject();
			this.toList = parseAddresses(msg.getRecipients(RecipientType.TO));
			this.ccList = parseAddresses(msg.getRecipients(RecipientType.CC));
			this.sentDate = msg.getSentDate();
			this.messageContent = getTextFromMessage(msg);
			this.contentType = msg.getContentType();
			this.mailid = mailid;
			grabTags();
			this.validMail = true;
		} catch (Exception e) {
			System.out.println("MIAU" + e);
		}
	}

	private void grabTags() {
		// Parse subject to tags that starts with "#" or "*"
		this.tags = subject.replaceAll("\\s+", "-").replaceAll(" ", "").replaceAll("([#*])", "_").replace("-_", "_").toString().replaceFirst("_", "");
	}

	/**
	 * Returns a list of addresses in String format separated by comma
	 *
	 * @param address an array of Address objects
	 * @return a string represents a list of addresses
	 */
	private String parseAddresses(Address[] address) {
		String listAddress = "";

		if (address != null) {
			for (int i = 0; i < address.length; i++) {
				listAddress += address[i].toString() + ", ";
			}
			if (listAddress.length() > 1) {
				listAddress = listAddress.substring(0, listAddress.length() - 2);
			}
		}
		return listAddress;
	}

	private String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "" + bodyPart.getContent();
				break; // without break same text appears twice in my tests
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "" + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return result;
	}

	public boolean isValid() {
		return validMail;
	}


	// Mail functions

	
	public boolean markAsRead(Boolean status) {
		try {
			msg.setFlag(Flags.Flag.SEEN, status);
			return true;
		} catch (MessagingException e) {
			conhandler.debug("Error at setting mail SEEN status to: " + '"' + status + '"');
			return false;
		}
	}
	
	
	
	
	// Return Mail as human readable string.
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message #" + (mailid + 1) + ":");
		builder.append("\n\t\t From: " + from + "\n");
		builder.append("\t\t To: " + toList + "\n");
		builder.append("\t\t CC: " + ccList + "\n");
		builder.append("\t\t Tags: " + tags + "\n");
		builder.append("\t\t Subject: " + subject + "\n");
		builder.append("\t\t Sent Date: " + sentDate + "\n");
		builder.append("\t\t Message: " + messageContent + "\n");
		return builder.toString();
	}

	// Redirect boolean from filehandler.saveMail back to MailGrabber
	public boolean saveMail(FileHandler filehandler) {
		return filehandler.saveMail(fromAddress, from, subject, tags, toList, ccList, sentDate, messageContent, msg, contentType);
	}
}
