package com.raf.asmi.karte.controllers;

import com.raf.asmi.karte.dto.Servis1KorisnikDto;

public class KartaUtils {
	public static double proracunajPopust(Servis1KorisnikDto korisnik) {
		if(korisnik.getRang().equalsIgnoreCase(Servis1KorisnikDto.RANG_ZLATO)) {
			return 0.2;
		}
		if(korisnik.getRang().equalsIgnoreCase(Servis1KorisnikDto.RANG_SREBRO)) {
			return 0.1;
		}
		return 0;
	}
}
