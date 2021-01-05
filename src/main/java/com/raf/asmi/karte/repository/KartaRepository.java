package com.raf.asmi.karte.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.raf.asmi.karte.entiteti.Karta;

public interface KartaRepository extends CrudRepository<Karta, Integer>, KartaRepositoryCustom{
	
}
