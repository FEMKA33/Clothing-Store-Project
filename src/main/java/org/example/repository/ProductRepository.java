package org.example.repository;

import org.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByBrand(String brand);

    List<Product> findTop10ByOrderBySalesCountDesc();

    List<Product> findTop10ByDiscountTrueOrderBySalesCountDesc();

    @Query(value = "SELECT * FROM products ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Product> findRandom10();
}