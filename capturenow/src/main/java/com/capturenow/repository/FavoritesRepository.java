package com.capturenow.repository;

import com.capturenow.module.Customer;
import com.capturenow.module.Favorites;
import com.capturenow.module.Photographer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, String> {

    List<Favorites> findByCustomerId(String customerId);
    boolean existsByCustomerAndPhotographer(Customer customer, Photographer photographer);

    Favorites findByCustomerAndPhotographer(Customer customer, Photographer photographer);

    @Query("SELECT DISTINCT f.photographer FROM Favorites f WHERE f.customer.id = :customerId")
    List<Photographer> findDistinctPhotographerByCustomerId(String customerId);
}
