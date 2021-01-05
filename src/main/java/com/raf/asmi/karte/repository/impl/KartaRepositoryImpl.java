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

}
