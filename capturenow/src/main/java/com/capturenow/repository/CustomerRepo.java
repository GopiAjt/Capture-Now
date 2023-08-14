package com.capturenow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.capturenow.module.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer>{

	@Query(value="select * from customer where email=?1",nativeQuery = true)
	public Customer findByEmail(String email);

	public Optional<Customer> findByName(String username);
}
