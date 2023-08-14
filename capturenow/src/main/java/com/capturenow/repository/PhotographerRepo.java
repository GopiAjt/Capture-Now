package com.capturenow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.capturenow.module.Photographer;

@Repository
public interface PhotographerRepo extends JpaRepository<Photographer, Integer> {
	
	public Photographer findByEmail(String email);

	public Optional<Photographer> findByName(String username);
	
}
