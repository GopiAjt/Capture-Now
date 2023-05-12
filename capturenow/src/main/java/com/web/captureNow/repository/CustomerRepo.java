package com.web.captureNow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.captureNow.module.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {

	@Query(value="select * from customer where cemail=?1",nativeQuery = true)
	Customer validate(String cemail);
}
