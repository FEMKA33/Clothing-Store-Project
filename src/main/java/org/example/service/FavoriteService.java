package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Favorite;
import org.example.model.Product;
import org.example.model.UserEntity;
import org.example.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public boolean toggle(UserEntity user, Product product) {
        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            favoriteRepository.deleteByUserAndProduct(user, product);
            return false;
        }
        Favorite fav = new Favorite();
        fav.setUser(user);
        fav.setProduct(product);
        favoriteRepository.save(fav);
        return true;
    }

    public List<Long> getFavoriteProductIds(UserEntity user) {
        return favoriteRepository.findAllByUser(user)
                .stream()
                .map(fav -> fav.getProduct().getId())
                .toList();
    }

    public List<Product> getFavorites(UserEntity user) {
        return favoriteRepository.findAllByUser(user)
                .stream()
                .map(Favorite::getProduct)
                .toList();
    }
}