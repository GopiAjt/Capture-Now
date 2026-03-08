package com.capturenow.serviceimpl;

import com.capturenow.config.ImageUtils;
import com.capturenow.dto.PhotographerCardDto;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.SearchBarService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Data
public class SearchBarImpl implements SearchBarService {

    @Autowired
    private final PhotographerRepo repo;

    public Page<PhotographerCardDto> searchByPreference(String preference, int offset, int pageSize) {
        List<PhotographerCardDto> photographerCardDtos = new ArrayList<>();
        Page<Photographer> photographers = repo.findAll(PageRequest.of(offset, pageSize));

        for (Photographer p : photographers) {
            Set<String> services = new HashSet<>(Arrays.asList(p.getServices().split(",\\s*|\\s+")));
            Set<String> languages = new HashSet<>(Arrays.asList(p.getLanguages().split(",\\s*|\\s+")));

            if (containsIgnoreCase(languages, preference) || containsIgnoreCase(services, preference)) {
                PhotographerCardDto photographerCardDto = new PhotographerCardDto();
                photographerCardDto.setId(p.getId());
                photographerCardDto.setName(p.getName());
                photographerCardDto.setExperience(p.getExperience());
                photographerCardDto.setLanguages(p.getLanguages());
                photographerCardDto.setServices(p.getServices());
                photographerCardDto.setAvgRating(p.getAvgRating());
                photographerCardDto.setServiceLocation(p.getServiceLocation());

                if (p.getProfilePhoto() != null) {
                    photographerCardDto.setProfilePhoto(ImageUtils.decompressImage(p.getProfilePhoto()));
                } else {
                    photographerCardDto.setProfilePhoto(null);
                }

                photographerCardDtos.add(photographerCardDto);
            }
        }

        return new PageImpl<>(photographerCardDtos, photographers.getPageable(), photographers.getTotalElements());
    }

    private boolean containsIgnoreCase(Set<String> set, String preference) {
        return set.stream().anyMatch(s -> s.equalsIgnoreCase(preference));
    }


    @Override
    public List<PhotographerCardDto> searchByLocation(String location) {
        List<PhotographerCardDto> photographerCardDtos = new ArrayList<>();
        List<Photographer> photographers = repo.findAll();

        for (Photographer p : photographers) {
            Set<String> services = new HashSet<>(Arrays.asList(p.getServiceLocation().split(" ")));

            if (containsIgnoreCase(services, location)) {  // Use lowercase for case-insensitive matching
                PhotographerCardDto photographerCardDto = new PhotographerCardDto();
                photographerCardDto.setName(p.getName());
                photographerCardDto.setExperience(p.getExperience());
                photographerCardDto.setLanguages(p.getLanguages());
                photographerCardDto.setServices(p.getServices());
                photographerCardDto.setEmail(p.getEmail());
                photographerCardDto.setAvgRating(p.getAvgRating());
                photographerCardDto.setServiceLocation(p.getServiceLocation());
                if (p.getProfilePhoto() != null){
                    photographerCardDto.setProfilePhoto(ImageUtils.decompressImage(p.getProfilePhoto()));
                }else {
                    photographerCardDto.setProfilePhoto(null);
                }
                photographerCardDtos.add(photographerCardDto);
            }
        }
        return photographerCardDtos;
    }
}