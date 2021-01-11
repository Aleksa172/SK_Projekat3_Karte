package com.raf.asmi.karte.dto;

import java.util.HashMap;

public class Servis2LetDto {
	
	public static final String STATUS_CANCELLED = "CANCELLED";

	private String status;
	private String pocetnaDestinacija;
	private String krajnjaDestinacija;
	private short duzina;
	private Double cena;
	private Integer letId;
	private Integer avionId;
	private String avionNaziv;
	
	
	public Servis2LetDto(HashMap<String,Object> httpResponse) {
		this.status = (String) httpResponse.get("status");
		this.pocetnaDestinacija = (String) httpResponse.get("pocetnaDestinacija");
		this.krajnjaDestinacija = (String) httpResponse.get("krajnjaDestinacija");
		this.duzina = ((Integer) httpResponse.get("duzinaLeta")).shortValue();
		this.cena = (Double) httpResponse.get("cena");
		this.letId = (Integer) httpResponse.get("id");
		
		// odgovor -> "avion" -> "id"
		this.avionId = (Integer) ((HashMap<String,Object>) httpResponse.get("avion")).get("id");
		this.avionNaziv = (String) ((HashMap<String,Object>) httpResponse.get("avion")).get("naziv");
	}
	
	
	
	
	
	public String getStatus() {
		return status;
	}





	public String getPocetnaDestinacija() {
		return pocetnaDestinacija;
	}





	public String getKrajnjaDestinacija() {
		return krajnjaDestinacija;
	}





	public short getDuzina() {
		return duzina;
	}





	public Integer getLetId() {
		return letId;
	}





	public Integer getAvionId() {
		return avionId;
	}





	public String getAvionNaziv() {
		return avionNaziv;
	}

	
	public Double getCena() {
		return cena;
	}
}
