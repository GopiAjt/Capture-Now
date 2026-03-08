package com.capturenow.serviceimpl;


import com.capturenow.config.ImageUtils;
import com.capturenow.dto.FavResponseDto;
import com.capturenow.module.Customer;
import com.capturenow.module.Favorites;
import com.capturenow.module.Photographer;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.repository.FavoritesRepository;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritesServiceImple implements FavoritesService {

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PhotographerRepo photographerRepo;

    @Override
    public String addToFavorites(String customerId, String photographerId) {
        Customer customer = customerRepo.findByEmail(customerId);
        Optional<Photographer> photographer = photographerRepo.findById(photographerId);

        // Check if the customer has already added the photographer to favorites
        boolean alreadyExists = favoritesRepository.existsByCustomerAndPhotographer(customer, photographer.get());
        if (alreadyExists) {
            return "Photographer is already added to favorites.";
        }

        Favorites favorites = new Favorites();
        favorites.setPhotographer(photographer.get());
        favorites.setCustomer(customer);

        favoritesRepository.save(favorites);

        return "Photographer added to favorites successfully.";
    }

    @Override
    public Boolean removeFromFavorites(String customerId, String photographerId) {
        Customer customer = customerRepo.findByEmail(customerId);
        Optional<Photographer> photographer = photographerRepo.findById(photographerId);

        // Check if the customer has added the photographer to favorites
        if (photographer.isPresent()){
            Favorites favorites = favoritesRepository.findByCustomerAndPhotographer(customer, photographer.get());
            if (favorites == null) {
                return false;
            }
            // Remove the photographer from favorites
            favoritesRepository.delete(favorites);
        }
        return true;
    }

    @Override
    public List<FavResponseDto> getAllFavorites(String emailId) {
        Customer  customer = customerRepo.findByEmail(emailId);
        if (customer!=null){
            List<Photographer> photographer = favoritesRepository.findDistinctPhotographerByCustomerId(customer.getId());
            List<FavResponseDto> favResponseDtos = new ArrayList<>(photographer.size());
            for (Photographer p : photographer) {
                FavResponseDto favResponseDto = new FavResponseDto();
                favResponseDto.setPId(p.getId());
                favResponseDto.setName(p.getName());
                favResponseDto.setProfilePhoto(ImageUtils.decompressImage(p.getProfilePhoto()));
                favResponseDto.setAvgRating(p.getAvgRating());
                favResponseDto.setStartsWith(p.getStartsWith());
                favResponseDtos.add(favResponseDto);
            }
            return favResponseDtos;
        }
        return null;
    }


    public List<Favorites> getFavoritesByCustomerId(String customerId) {
        System.out.println(favoritesRepository.findByCustomerId(customerId));
        return favoritesRepository.findByCustomerId(customerId);
    }

}
