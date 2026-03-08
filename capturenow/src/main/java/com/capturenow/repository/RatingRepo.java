package com.capturenow.repository;

import com.capturenow.module.Photographer;
import com.capturenow.module.PhotographerRatings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepo extends JpaRepository<PhotographerRatings, String> {

    @Query("SELECT AVG(r.ratings) FROM PhotographerRatings r WHERE r.photographer = ?1")
    Double getAverageRatingByPhotographer(Photographer photographer);

    List<PhotographerRatings> getRatingsByPhotographer(Photographer photographer);
}
