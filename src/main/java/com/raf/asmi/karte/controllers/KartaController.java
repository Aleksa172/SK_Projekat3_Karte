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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.raf.asmi.karte.HttpCommunicationUtils;
import com.raf.asmi.karte.dto.KupovinaKarteResp;
import com.raf.asmi.karte.dto.S1ChangeForm;
import com.raf.asmi.karte.dto.Servis1KorisnikDto;
import com.raf.asmi.karte.dto.Servis2LetDto;
import com.raf.asmi.karte.entiteti.Karta;
import com.raf.asmi.karte.entiteti.KartaStatus;
import com.raf.asmi.karte.repository.KartaRepository;
import com.raf.asmi.karte.repository.impl.KartaRepositoryImpl;
import com.raf.asmi.karte.security.SecurityConstants;

@RestController
public class KartaController {
	
	@Autowired
	private KartaRepository kartaRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue obrisanLetQueue;
	
	@GetMapping("/moje_karte")
	public List<Karta> pregledKarata(@RequestHeader(value = SecurityConstants.HEADER_STRING) String token){
		String email = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
		
		return kartaRepository.vratiKarteZaKorisnika(email); 
		
	}
	
	@PostMapping("/zapocni_kupovinu/{letId}")
	public KupovinaKarteResp zapocetaKupovina(@PathVariable(required = true) Integer letId,
			@RequestHeader(value = SecurityConstants.HEADER_STRING) String token){
		String email = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();

		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization",token);
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		
		
		// Pitamo Servis2 za podatke o kupovini
		// HashMap<String,Object> s = rt.getForObject("http://localhost:8082/let/"+letId, HashMap.class, entity);
		ResponseEntity<HashMap> resp1 = rt.exchange("http://localhost:8082/let/"+letId, HttpMethod.GET, entity, HashMap.class);
		HashMap<String,Object> s = resp1.getBody();
		
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
		
		ResponseEntity<String> resp2 = rt.exchange("http://localhost:8081/whoAmI", HttpMethod.GET, entity, String.class); 
		String korisniciResponse = resp2.getBody(); 
		
		Servis1KorisnikDto korisnikData = new Servis1KorisnikDto(korisniciResponse);
		
		ResponseEntity<Integer> resp3 = rt.exchange("http://localhost:8082/let/"+letId+"/preostalo-mesta", HttpMethod.GET, entity, Integer.class);
		Integer preostalo = resp3.getBody();
		
		double popust = KartaUtils.proracunajPopust(korisnikData);
		
		// Ako je popust 20% - dobijamo 0.2, dakle cenu treba pomnoziti sa 0.8
		// to je 1 - popust
		double finalCena = letData.getCena()*(1-popust);
		String stringPopust = Math.floor(popust*100)+"%";
		
		return new KupovinaKarteResp("ok", stringPopust, finalCena, preostalo);
	}

	@PostMapping("/finalizuj_kupovinu/{letId}")
	public KupovinaKarteResp finalizujKupovinu(@PathVariable(required = true) Integer letId,
			@RequestHeader(value = SecurityConstants.HEADER_STRING) String token){
		String email = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build()
				.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
		
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization",token);
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		
		// Pitamo Servis2 za podatke o kupovini
		ResponseEntity<HashMap> resp1 = rt.exchange("http://localhost:8082/let/"+letId, HttpMethod.GET, entity, HashMap.class);
		HashMap<String,Object> s = resp1.getBody();
		
		// Odgovor je parsiran kao null ako je server bacio gresku
		if(s == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trazeni let ne postoji");
		}
		
		Servis2LetDto letData = new Servis2LetDto(s);
		
		// Zauzimamo mesta
		ResponseEntity<Integer> response = HttpCommunicationUtils.sendPost("http://localhost:8082/let/"+letId+"/zauzmi-mesta", entity);
		Integer preostalo = response.getBody();
		
		// Odgovor je parsiran kao null ako je server bacio gresku
		if(preostalo == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doslo je do greske - let ne postoji ili nema vise mesta");
		}
		
		ResponseEntity<String> resp2 = rt.exchange("http://localhost:8081/whoAmI", HttpMethod.GET, entity, String.class); 
		String korisniciResponse = resp2.getBody(); 
		
		Servis1KorisnikDto korisnikData = new Servis1KorisnikDto(korisniciResponse);
		
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
		// Dodaj milje
		// Neophodno je spakovati JSON objekat koji odgovara S1 ChangeForm
		HttpHeaders headers2 = new HttpHeaders();
		headers2.set("Authorization",token);
		headers2.setContentType(MediaType.APPLICATION_JSON);
		
		System.out.println(new S1ChangeForm(letData.getDuzina()).toJson());
		
		HttpEntity<String> entity2 = new HttpEntity<>(new S1ChangeForm(letData.getDuzina()).toJson(), headers2);
		ResponseEntity<String> response2 = HttpCommunicationUtils.sendPostExpectString("http://localhost:8081/addMile", entity2);
		
		
		return new KupovinaKarteResp("ok", stringPopust, finalCena, preostalo);
	}
	
}
