package com.raf.asmi.karte.dto;

public class KupovinaKarteResp {
	private String response;
	private String kolicinaPopusta;
	private Double konacnaCena;
	private Integer preostaloMesta;
	
	public KupovinaKarteResp(String response, String kolicinaPopusta, Double konacnaCena, Integer preostaloMesta) {
		this.response=response;
		this.kolicinaPopusta=kolicinaPopusta;
		this.konacnaCena=konacnaCena;
		this.preostaloMesta=preostaloMesta;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getKolicinaPopusta() {
		return kolicinaPopusta;
	}

	public void setKolicinaPopusta(String kolicinaPopusta) {
		this.kolicinaPopusta = kolicinaPopusta;
	}

	public Double getKonacnaCena() {
		return konacnaCena;
	}

	public void setKonacnaCena(double konacnaCena) {
		this.konacnaCena = konacnaCena;
	}
	
	public Integer getPreostaloMesta() {
		return preostaloMesta;
	}
	
	public void setPreostaloMesta(Integer preostaloMesta) {
		this.preostaloMesta = preostaloMesta;
	}
	
	
}
