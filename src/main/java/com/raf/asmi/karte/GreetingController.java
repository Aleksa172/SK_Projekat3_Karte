package com.raf.asmi.karte;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.raf.asmi.karte.entiteti.Karta;
import com.raf.asmi.karte.repository.KartaRepository;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	@Autowired
	private KartaRepository kartaRepository;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue testQueue;


	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
	
	@GetMapping("/test")
	public String test(@RequestParam(value = "name", defaultValue = "123") String name) {
		/*Let l = new Let();
		l.setPocetnaDestinacija("Stockholm");
		l.setKrajnjaDestinacija("New York");
		l.setTrajanjeLeta((short) 340);
		letRepository.save(l);
		*/
		/*
		Avion a = new Avion();
		a.setKapacitet((short) 96); 
		a.setNaziv("Ekatarina Velika");
		avionRepository.save(a);
		*/
		
		List<Karta> karte = kartaRepository.vratiKarteZaLet(2);
		
		//Optional<Avion> aTemp = avionRepository.findById(3);
		//Avion a = aTemp.get();
		//l.setAvion(a);
		//letRepository.save(l);
		return karte.toString();
	}
	
	@GetMapping("/test2")
	public HashMap<String,String> test2() {
		HashMap<String, String> map = new HashMap<>();
		map.put("key1", "val1");
		map.put("key2", "val2");
		return map;
	}
	
	@GetMapping("/test-call")
	public String test3() {
		RestTemplate rt = new RestTemplate();
		String s = rt.getForObject("http://localhost:8080/letovi", String.class);
		return s;
	}
	
	@GetMapping("/test-message")
	public String test4(@RequestParam(value="msg", defaultValue="undefined-msg") String msg){
		jmsTemplate.convertAndSend(testQueue, msg);
		return "ok";
	}
	
	@GetMapping("/test-login")
	public String test5() {
		return "";
	}
}
