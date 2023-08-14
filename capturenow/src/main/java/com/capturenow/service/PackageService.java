package com.capturenow.service;

import java.util.List;

import com.capturenow.dto.PackageDto;
import com.capturenow.module.Packages;

public interface PackageService {
	
	List<Packages> addPackage(PackageDto p);
	
	String deletePackage(int id);
	
	List<Packages> getAllPackages(String email);
	
}
