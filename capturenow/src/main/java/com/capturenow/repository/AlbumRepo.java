package com.capturenow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface AlbumRepo extends JpaRepository<Albums, String>{

	Optional<Albums> findByName(String fileName);

    Page<Albums> findByPhotographerAndCategory(Photographer photographer, String category, Pageable pageable);

    Page<Albums> findByPhotographerAndCategoryNot(Photographer photographer, String category, Pageable pageable);

}
