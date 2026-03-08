package com.capturenow.serviceimpl;

import com.capturenow.dto.PhotographerCardDto;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PhotographerRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Service
@Data
public class PhotographerSorter {

    @Autowired
    private final PhotographerRepo photographerRepo;

    // Filter photographers based on multiple criteria
    public List<PhotographerCardDto> addFilter(String location, Integer cost, Integer rating, List<PhotographerCardDto> photographers) {

        // Define comparator for sorting
        Comparator<PhotographerCardDto> comparator = Comparator.comparing(PhotographerCardDto::getStartsWith);

        if (cost != null) {
            switch (cost) {
                case 1:
                    comparator = Comparator.comparing(PhotographerCardDto::getStartsWith).reversed();
                    break;
                default:
                    // Handle other cases if needed
                    break;
            }
        }

        if (rating != null) {
            switch (rating) {
                case 1:
                    comparator = comparator.thenComparing(PhotographerCardDto::getAvgRating).reversed();
                    break;
                default:
                    comparator = comparator.thenComparing(PhotographerCardDto::getAvgRating);
                    break;
            }
        }

        // Sort the photographers
        Collections.sort(photographers, comparator);

        return photographers;
    }

    public List<Photographer> sortPhotographerByCostAsc(String cost){
        return photographerRepo.findAll(Sort.by(Sort.Direction.ASC, cost));
    }

    public Page<Photographer> sortPhotographerByCostAscWithPagination(int offset, int pageSize, String field){
        return photographerRepo.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(Sort.Direction.ASC, field)));
    }
}
