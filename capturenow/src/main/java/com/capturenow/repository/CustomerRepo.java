package com.capturenow.repository;

import com.capturenow.module.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, String> {

    @Query(value = "select * from customer where email=?1", nativeQuery = true)
    public Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c JOIN FETCH c.favorites WHERE c.email = :email")
    Customer findByEmailWithFavorites(@Param("email") String email);
    public Optional<Customer> findByName(String username);

}
