package com.capturenow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capturenow.module.Packages;

public interface PackageRepo extends JpaRepository<Packages, Integer>{
	
	
}
