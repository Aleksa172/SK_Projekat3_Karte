package com.raf.asmi.karte.dto;

import java.util.HashMap;

public class Servis1KorisnikDto {
	
	public static final String RANG_ZLATO = "Zlato";
	public static final String RANG_SREBRO = "Srebro";
	public static final String RANG_BRONZA = "Bronza";

	
	private Integer id;
	private String ime;
	private String prezime;
	private String brojPasosa;
	private String email;
	private String rang;
	
	public Servis1KorisnikDto(HashMap<String, Object> httpResponse) {
		// - Ovo izmeniti u skladu sa Servis1 specifikacijom - kada bude bio gotov
		this.id = (Integer) httpResponse.get("id");
		this.ime = (String) httpResponse.get("ime");
		this.prezime = (String) httpResponse.get("prezime");
		this.brojPasosa = (String) httpResponse.get("brojPasosa");
		this.email = (String) httpResponse.get("email");
		this.rang = (String) httpResponse.get("rang");
	}
	
	
	public Servis1KorisnikDto(String s1Response) {
		String[] parts = s1Response.split("/");
		this.id = 42; // Ovo izmeniti kada servis1 vrati id korisnika
		this.ime = parts[0];
		this.prezime = parts[1];
		this.email = parts[2];
		this.brojPasosa = parts[3]+"";
		this.rang = parts[4];
	}
	
	public Integer getId() {
		return id;
	}
	public String getIme() {
		return ime;
	}
	public String getPrezime() {
		return prezime;
	}
	public String getBrojPasosa() {
		return brojPasosa;
	}
	public String getEmail() {
		return email;
	}
	public String getRang() {
		return rang;
	}
	
	
}
