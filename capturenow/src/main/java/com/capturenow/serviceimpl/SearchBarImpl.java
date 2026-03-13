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
import java.util.stream.Collectors;
import com.capturenow.module.Packages;
import java.util.Comparator;

@Service
@Data
public class SearchBarImpl implements SearchBarService {

    private final PhotographerRepo repo;

    @Override
    public Page<PhotographerCardDto> searchByPreference(String preference, int offset, int pageSize) {
        PageRequest pageRequest = PageRequest.of(offset, pageSize);
        Page<Photographer> photographers = repo.searchPhotographer(preference, pageRequest);
        
        return photographers.map(this::mapToCardDto);
    }

    private PhotographerCardDto mapToCardDto(Photographer p) {
        PhotographerCardDto dto = new PhotographerCardDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setExperience(p.getExperience());
        dto.setLanguages(p.getLanguages());
        dto.setServices(p.getServices());
        dto.setAvgRating(p.getAvgRating());
        dto.setServiceLocation(p.getServiceLocation());
        dto.setEmail(p.getEmail());

        List<Packages> packages = p.getPackages();
        if (packages != null && !packages.isEmpty()) {
            packages.sort(Comparator.comparingDouble(Packages::getEventRate));
            dto.setStartsWith(packages.get(0).getEventRate());
        } else {
            dto.setStartsWith(0.0);
        }

        if (p.getProfilePhoto() != null) {
            dto.setProfilePhoto(ImageUtils.decompressImage(p.getProfilePhoto()));
        } else {
            dto.setProfilePhoto(null);
        }
        return dto;
    }




    @Override
    public List<PhotographerCardDto> searchByLocation(String location) {
        List<Photographer> photographers = repo.searchPhotographer(location, PageRequest.of(0, 100)).getContent();
        return photographers.stream()
                .map(this::mapToCardDto)
                .collect(Collectors.toList());
    }
}