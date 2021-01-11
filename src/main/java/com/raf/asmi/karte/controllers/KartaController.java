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
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.raf.asmi.karte.HttpCommunicationUtils;
import com.raf.asmi.karte.dto.KupovinaKarteResp;
import com.raf.asmi.karte.dto.Servis1KorisnikDto;
import com.raf.asmi.karte.dto.Servis2LetDto;
import com.raf.asmi.karte.entiteti.Karta;
import com.raf.asmi.karte.entiteti.KartaStatus;
import com.raf.asmi.karte.repository.KartaRepository;
import com.raf.asmi.karte.repository.impl.KartaRepositoryImpl;

@RestController
public class KartaController {
	
	@Autowired
	private KartaRepository kartaRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue obrisanLetQueue;
	
	@GetMapping("/moje_karte")
	public List<Karta> pregledKarata() {
		// Uzeti iz JWT usera
		Integer korisnikId = 12;
		
		return kartaRepository.vratiKarteZaKorisnika(korisnikId); 
		
	}
	
	@PostMapping("/zapocni_kupovinu/{letId}")
	public KupovinaKarteResp zapocetaKupovina(@PathVariable(required = true) Integer letId) {
		RestTemplate rt = new RestTemplate();
		// Pitamo Servis2 za podatke o kupovini
		HashMap<String,Object> s = rt.getForObject("http://localhost:8082/let/"+letId, HashMap.class);
		
		// Odgovor je parsiran kao null ako je server bacio gresku
		if(s == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trazeni let ne postoji");
		}
		
		Servis2LetDto letData = new Servis2LetDto(s);
		System.out.println(s);
		if(letData.getStatus().equalsIgnoreCase(Servis2LetDto.STATUS_CANCELLED)) {
			return new KupovinaKarteResp("error - Flight is cancelled", null, null, null);
		}
		
		// Pitamo Servis1 za podatke o korisniku
		// Trenutno fejkujemo odgovor
		
		HashMap<String,Object> zlatni = new HashMap<>();
		zlatni.put("id", 12);
		zlatni.put("ime", "Pera");
		zlatni.put("prezime", "Peric");
		zlatni.put("brojPasosa", "000123634");
		zlatni.put("email", "pera@pera.com");
		zlatni.put("rang", "zlato");
		
		Servis1KorisnikDto korisnikData = new Servis1KorisnikDto(zlatni);
		Integer preostalo = rt.getForObject("http://localhost:8082/let/"+letId+"/preostalo-mesta", Integer.class);
		
		double popust = KartaUtils.proracunajPopust(korisnikData);
		
		// Ako je popust 20% - dobijamo 0.2, dakle cenu treba pomnoziti sa 0.8
		// to je 1 - popust
		double finalCena = letData.getCena()*(1-popust);
		String stringPopust = Math.floor(popust*100)+"%";
		
		return new KupovinaKarteResp("ok", stringPopust, finalCena, preostalo);
	}

	@PostMapping("/finalizuj_kupovinu/{letId}")
	public KupovinaKarteResp finalizujKupovinu(@PathVariable(required = true) Integer letId) {
		RestTemplate rt = new RestTemplate();
		// Pitamo Servis2 za podatke o kupovini
		HashMap<String,Object> s = rt.getForObject("http://localhost:8082/let/"+letId, HashMap.class);
		
		// Odgovor je parsiran kao null ako je server bacio gresku
		if(s == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trazeni let ne postoji");
		}
		
		Servis2LetDto letData = new Servis2LetDto(s);
		
		// Zauzimamo mesta
		ResponseEntity<Integer> response = HttpCommunicationUtils.sendPost("http://localhost:8082/let/"+letId+"/zauzmi-mesta", null);
		Integer preostalo = response.getBody();
		
		// Odgovor je parsiran kao null ako je server bacio gresku
		if(preostalo == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doslo je do greske - let ne postoji ili nema vise mesta");
		}
		
		// Pitamo Servis1 za podatke o korisniku
		// Trenutno fejkujemo odgovor
		
		HashMap<String,Object> zlatni = new HashMap<>();
		zlatni.put("id", 13);
		zlatni.put("ime", "Jova");
		zlatni.put("prezime", "Jovic");
		zlatni.put("brojPasosa", "0001548099");
		zlatni.put("email", "jova@jova.com");
		zlatni.put("rang", "srebro");
		
		Servis1KorisnikDto korisnikData = new Servis1KorisnikDto(zlatni);
		
		double popust = KartaUtils.proracunajPopust(korisnikData);
		
		// Ako je popust 20% - dobijamo 0.2, dakle cenu treba pomnoziti sa 0.8
		// to je 1 - popust
		System.out.println(letData.getCena());
		double finalCena = letData.getCena()*(1-popust);
		String stringPopust = Math.floor(popust*100)+"%";
		
		Karta k = new Karta(letData.getLetId(), 
				korisnikData.getId(), 
				letData.getAvionNaziv(), 
				letData.getPocetnaDestinacija(), 
				letData.getKrajnjaDestinacija(), 
				letData.getDuzina(), 
				letData.getCena(),
				korisnikData.getIme(), 
				korisnikData.getPrezime(),
				korisnikData.getEmail(),
				korisnikData.getBrojPasosa());
		
		kartaRepository.save(k);
		
		return new KupovinaKarteResp("ok", stringPopust, finalCena, preostalo);
	}
	
}
