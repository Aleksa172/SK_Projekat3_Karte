package com.raf.asmi.karte.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.jms.Queue;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.raf.asmi.karte.entiteti.Karta;
import com.raf.asmi.karte.entiteti.KartaStatus;
import com.raf.asmi.karte.repository.KartaRepository;
import com.raf.asmi.karte.repository.impl.KartaRepositoryImpl;

@RestController
public class KartaController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue obrisanLetQueue;

	
	
	private void sendCancellationEmail(String destination, Karta karta) throws AddressException, MessagingException {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.mailtrap.io");
		prop.put("mail.smtp.port", "25");
		prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
		
		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication("acdcb8aae49500", "f9513570a4444f");
		    }
		});
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("noreply@skprojekat2.com"));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse(destination));
		message.setSubject("Mail Subject");

		String msg = "Vas let "+karta.getPocetnaDestinacija()+" - "+karta.getKrajnjaDestinacija()+" je nazalost otkazan <br/>" +
			"Uskoro ce vam biti povracen novac.";

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		message.setContent(multipart);

		Transport.send(message);
	}
}
