package org.example.repository;

import org.example.model.Product;
import org.example.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.model.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(UserEntity user);
    Optional<CartItem> findByUserAndProduct(UserEntity user, Product product);
}