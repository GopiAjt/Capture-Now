package com.capturenow.serviceimpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.capturenow.dto.PackageDto;
import com.capturenow.module.Packages;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PackageRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.PackageService;
import lombok.Data;

@Log4j2
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

		// Save the new package
//		packageRepo.save(packages);

		// Update the list of packages for the photographer
		List<Packages> list = photo.getPackages();
		list.add(packages);
		list.sort(Comparator.comparingDouble(Packages::getEventRate));
		photo.setPackages(list); // Update the list of packages
		photo.setStartsWith(list.isEmpty() ? 0 : list.get(0).getEventRate());

		// Save the updated photographer entity
		photographerRepo.save(photo);

		return list;
	}


	@Override
	public String deletePackage(String id) {
		Optional<Packages> p = packageRepo.findById(id);
		if (p.isPresent()) {
			packageRepo.delete(p.get());
			log.info("deleted");
			return "package deleted";
		}
		return "error";
	}

	@Override
	public List<Packages> getAllPackages(String email) {
		Optional<Photographer> p = photographerRepo.findById(email);
        return p.get().getPackages();
	}

}
