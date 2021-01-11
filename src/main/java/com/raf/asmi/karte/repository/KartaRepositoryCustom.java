package com.raf.asmi.karte.repository;

import java.util.HashMap;
import java.util.List;

import com.raf.asmi.karte.entiteti.Karta;

public interface KartaRepositoryCustom {
	public List<Karta> vratiKarteZaKorisnika(String usr_email);
	public List<Karta> vratiKarteZaLet(Integer let_id);
	
}
