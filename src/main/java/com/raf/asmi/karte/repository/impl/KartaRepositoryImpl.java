package com.raf.asmi.karte.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import com.raf.asmi.karte.entiteti.Karta;
import com.raf.asmi.karte.repository.KartaRepository;

public class KartaRepositoryImpl extends SimpleJpaRepository<Karta, Integer> implements KartaRepository{

	@PersistenceContext
	private EntityManager em;

	public KartaRepositoryImpl(EntityManager em) {
		super(Karta.class, em);
		this.em = em;
	}

	@Override
	public List<Karta> vratiKarteZaKorisnika(String usr_email) {
		Query q = em.createQuery("SELECT k FROM Karta k WHERE k.korisnikEmail=:korisnikEmail ORDER BY k.datumKupovine DESC");
		q.setParameter("korisnikEmail", usr_email);
		
		return q.getResultList();
	}

	@Override
	public List<Karta> vratiKarteZaLet(Integer let_id) {
		Query q = em.createQuery("SELECT k FROM Karta k WHERE k.letId=:let_id");
		q.setParameter("let_id", let_id);
		
		return q.getResultList();
	}

}
