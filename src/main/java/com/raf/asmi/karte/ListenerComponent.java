package com.raf.asmi.karte;

import java.util.List;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.raf.asmi.karte.entiteti.Karta;
import com.raf.asmi.karte.entiteti.KartaStatus;
import com.raf.asmi.karte.repository.KartaRepository;

@Component
public class ListenerComponent {
	
	@Autowired
	private KartaRepository kartaRepository;
	

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue obrisanLetS1Queue;

	@JmsListener(destination="test.queue")
	public void consume(String message){
		System.out.println("Message received "+message);
	}
	
	@JmsListener(destination="obrisanlet.queue")
	public void obrisanLet(String id) {
		// Integer nije trusted
		System.out.println("OBRISAN LET - PRIMIO S3 "+id);
		
		List<Karta> pogodjeneKarte = kartaRepository.vratiKarteZaLet(Integer.parseInt(id));
		for(Karta k:pogodjeneKarte) {
			k.setStatus(KartaStatus.CANCELLED);
			kartaRepository.save(k);
			System.out.println("SERVIS 3: Poslata poruka S1 i proslednje email");
			System.out.println("PORUKA KA S1: "+k.getKorisnik_email()+"/"+k.getDuzinaLeta());
			jmsTemplate.convertAndSend(obrisanLetS1Queue, k.getKorisnik_email()+"/"+k.getDuzinaLeta());
			try {
				sendCancellationEmail(k.getKorisnik_email(), k);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
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
