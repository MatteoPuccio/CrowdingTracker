package com.gpsreminder.services;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailService {

	private static EmailService instance = null;

	private JavaMailSenderImpl emailSender;
	
	private static final String from = "crowdingtracker@gmail.com";
	private static final String appPassword = "#########";
	
	private Authenticator auth;
	private Properties props;

	public void sendEmail(String to, String subject, String text) {
		Session session = Session.getInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		
		try {
			message.addHeader("Content-type", "text/HTML; charset=UTF-8");
			message.addHeader("format", "flowed");
			message.addHeader("Content-Transfer-Encoding", "8bit");
		      
			message.setFrom(new InternetAddress(from, "NoReply-JD"));
			message.setReplyTo(InternetAddress.parse(to, false));
			message.setSubject(subject, "UTF-8");
			message.setText(text, "UTF-8");
			message.setSentDate(new Date());
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			
			Transport.send(message); 
			
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendRegistrationTokenEmail(String token, String to, Locale locale) {
		I18n.setLocale(locale);
		
		String subject = I18n.getMessage("EmailRegistrationSubject");
		String text = I18n.getMessage("EmailRegistrationText1") + ": " +  token + "\n"
				+ I18n.getMessage("EmailRegistrationText2");
		
		sendEmail(to, subject, text);
	}

	private EmailService() {
		emailSender = new JavaMailSenderImpl();

		emailSender.setHost("smtp.gmail.com");
		emailSender.setPort(587);
		
		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); 
		props.put("mail.smtp.port", "587"); 
		props.put("mail.smtp.auth", "true"); 
		props.put("mail.smtp.starttls.enable", "true"); 
		
		auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, appPassword);
			}
		};
		
	}

	public static EmailService getInstance() {
		if (instance == null)
			instance = new EmailService();
		return instance;
	}
}