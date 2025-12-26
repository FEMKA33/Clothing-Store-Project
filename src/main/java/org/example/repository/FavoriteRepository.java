package org.example.repository;

import org.example.model.Favorite;
import org.example.model.Product;
import org.example.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndProduct(UserEntity user, Product product);

    void deleteByUserAndProduct(UserEntity user, Product product);

    List<Favorite> findAllByUser(UserEntity user);

    @Query("select f.product.id from Favorite f where f.user = :user")
    List<Long> findProductIdsByUser(@Param("user") UserEntity user);
}