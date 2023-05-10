package com.capturenow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.capturenow.module.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer>{

	@Query(value="select * from customer where cemail=?11",nativeQuery = true)
	List<Customer> findByEmail(String email);
}
