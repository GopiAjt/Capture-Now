package com.capturenow.service;

import com.capturenow.dto.PhotographerCardDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchBarService {
    public Page<PhotographerCardDto> searchByPreference(String preference, int offset, int pageSize);
    List<PhotographerCardDto> searchByLocation(String preference);
}