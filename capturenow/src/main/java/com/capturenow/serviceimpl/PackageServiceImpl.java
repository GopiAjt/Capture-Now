package com.capturenow.serviceimpl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capturenow.dto.PackageDto;
import com.capturenow.module.Packages;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PackageRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.PackageService;
import lombok.Data;

@Data
@Service
public class PackageServiceImpl implements PackageService{

	@Autowired
	private final PackageRepo packageRepo;
	
	@Autowired
	private final PhotographerRepo photographerRepo;
	
	@Override
	public List<Packages> addPackage(PackageDto p) {
		
		Packages packages = new Packages();
		packages.setPackageName(p.getPackageName());
		packages.setCategory(p.getCategory());
		packages.setEventRate(p.getEventRate());
		packages.setOneDayRate(p.getOneDayRate());
		packages.setOneHourRate(p.getOneHourRate());
		packages.setVideoRate(p.getVideoRate());
		packages.setDescription(p.getDescription());
		Photographer photo = photographerRepo.findByEmail(p.getEmail());
		packages.setPhotographer(photo);
		packageRepo.save(packages);
		List<Packages> list = photo.getPackages();
		list.add(packages);
		photographerRepo.save(photo);
		return list;
	}

	@Override
	public String deletePackage(int id) {
		Optional<Packages> p = packageRepo.findById(id);
		packageRepo.delete(p.get());
		return "package deleted";
	}

	@Override
	public List<Packages> getAllPackages(String email) {
		Photographer p = photographerRepo.findByEmail(email);
		return p.getPackages();
	}

}
