package com.capturenow.repository;

import com.capturenow.dto.PhotographerCardDto;
import com.capturenow.module.Photographer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotographerRepo extends JpaRepository<Photographer, String> {
    @Query("SELECT new com.capturenow.dto.PhotographerCardDto(p.name, p.email, p.serviceLocation, p.experience, p.services, p.languages, p.profilePhoto, p.startsWith, p.avgRating) FROM Photographer p")
    public List<PhotographerCardDto> findAllPhotographers();

    public Photographer findByEmail(String email);

    @Query("SELECT p FROM Photographer p WHERE " +
            "LOWER(p.name) LIKE CONCAT('%', LOWER(:query), '%') " +
            "OR LOWER(p.services) LIKE CONCAT('%', LOWER(:query), '%') " +
            "OR LOWER(p.email) LIKE CONCAT('%', LOWER(:query), '%')"+
            "OR LOWER(p.languages) LIKE CONCAT('%', LOWER(:query), '%')"+
            "OR LOWER(p.serviceLocation) LIKE CONCAT('%', LOWER(:query), '%')")
    List<Photographer> searchPhotographer(@Param("query") String query);

}
