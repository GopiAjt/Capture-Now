package com.capturenow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capturenow.module.Albums;

@Repository
public interface AlbumRepo extends JpaRepository<Albums, Integer>{

	Optional<Albums> findByName(String fileName);
	
	Albums findById(int id);
}
