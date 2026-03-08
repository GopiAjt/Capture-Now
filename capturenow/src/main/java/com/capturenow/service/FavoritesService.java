package com.capturenow.service;

import com.capturenow.dto.FavResponseDto;
import com.capturenow.module.Customer;
import com.capturenow.module.Favorites;
import com.capturenow.module.Photographer;

import java.util.List;

public interface FavoritesService {

    String addToFavorites(String customer, String photographer);

    List<Favorites> getFavoritesByCustomerId(String customerId);

    Boolean removeFromFavorites(String customerId, String photographerId);

    List<FavResponseDto> getAllFavorites(String emailId);

}
