package com.raf.asmi.karte.entiteti;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.transaction.Transactional;

@Entity
public class Karta {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer letId;
	private Integer korisnikId;

	private String avion_ime;
	private String pocetnaDestinacija;
	private String krajnjaDestinacija;
	private short duzinaLeta; // u km
	private double cena; // u evrima
	@Enumerated(EnumType.STRING) // Stanje leta - OPEN, CANCELLED
	private KartaStatus status;
	private String korisnik_ime;
	private String korisnik_prezime;
	private String korisnik_email;
	private String korisnik_brojPasosa;
	@Temporal(TemporalType.TIMESTAMP)
	private Date datumKupovine;
	
	public Karta() {
		
	}
	
	public Karta(Integer letId, 
				Integer korisnikId,
				String avion_ime,
				String pocetnaDestinacija,
				String krajnjaDestinacija,
				short duzinaLeta,
				double cena,
				String korisnik_ime,
				String korisnik_prezime,
				String korisnik_email,
				String korisnik_brojPasosa){
		this.letId=letId;
		this.korisnikId=korisnikId;
		this.avion_ime=avion_ime;
		this.pocetnaDestinacija=pocetnaDestinacija;
		this.krajnjaDestinacija=krajnjaDestinacija;
		this.duzinaLeta=duzinaLeta;
		this.cena=cena;
		this.korisnik_ime=korisnik_ime;
		this.korisnik_prezime=korisnik_prezime;
		this.korisnik_email=korisnik_email;
		this.korisnik_brojPasosa=korisnik_brojPasosa;
		
		
		this.status=KartaStatus.OPEN;
		this.datumKupovine=new Date();
	}
	
	public Integer getId() {
		return id;
	}
	public String getPocetnaDestinacija() {
		return pocetnaDestinacija;
	}
	public void setPocetnaDestinacija(String pocetnaDestinacija) {
		this.pocetnaDestinacija = pocetnaDestinacija;
	}
	public String getKrajnjaDestinacija() {
		return krajnjaDestinacija;
	}
	public void setKrajnjaDestinacija(String krajnjaDestinacija) {
		this.krajnjaDestinacija = krajnjaDestinacija;
	}
	public short getDuzinaLeta() {
		return duzinaLeta;
	}
	public void setDuzinaLeta(short duzinaLeta) {
		this.duzinaLeta = duzinaLeta;
	}
	public double getCena() {
		return cena;
	}
	public void setCena(double cena) {
		this.cena = cena;
	}
	public KartaStatus getStatus() {
		return status;
	}
	public void setStatus(KartaStatus status) {
		this.status = status;
	}
	public String getAvion_ime() {
		return avion_ime;
	}
	public void setAvion_ime(String avion_ime) {
		this.avion_ime = avion_ime;
	}
	public String getKorisnik_ime() {
		return korisnik_ime;
	}
	public void setKorisnik_ime(String korisnik_ime) {
		this.korisnik_ime = korisnik_ime;
	}
	public String getKorisnik_prezime() {
		return korisnik_prezime;
	}
	public void setKorisnik_prezime(String korisnik_prezime) {
		this.korisnik_prezime = korisnik_prezime;
	}
	public String getKorisnik_email() {
		return korisnik_email;
	}
	public void setKorisnik_email(String korisnik_email) {
		this.korisnik_email = korisnik_email;
	}
	public String getKorisnik_brojPasosa() {
		return korisnik_brojPasosa;
	}
	public void setKorisnik_brojPasosa(String korisnik_brojPasosa) {
		this.korisnik_brojPasosa = korisnik_brojPasosa;
	}
	public Date getDatumKupovine() {
		return datumKupovine;
	}
	public void setDatumKupovine(Date datumKupovine) {
		this.datumKupovine = datumKupovine;
	}
	
	public Integer getLetId() {
		return letId;
	}
	
	public void setLetId(Integer let_id) {
		this.letId = let_id;
	}
	
	public Integer getKorisnikId() {
		return korisnikId;
	}
	
	public void setKorisnikId(Integer korisnikId) {
		this.korisnikId = korisnikId;
	}
	
	
	
}
