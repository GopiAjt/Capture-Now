package com.capturenow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capturenow.module.Albums;

@Repository
public interface AlbumRepo extends JpaRepository<Albums, String>{

	Optional<Albums> findByName(String fileName);

	void deleteById(Integer id);

	Optional<Albums> findById(String id);
}
