package com.capturenow.repository;

import com.capturenow.module.PhotographerKycDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotographerKycRepo extends JpaRepository<PhotographerKycDetails, String> {

}
