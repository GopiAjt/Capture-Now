package com.capturenow.repository;

import com.capturenow.module.PhotographerRatings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepo extends JpaRepository<PhotographerRatings, Integer> {

}
