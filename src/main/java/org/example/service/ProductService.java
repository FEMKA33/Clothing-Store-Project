package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> filter(
            String q,
            String category,
            String brand,
            Double maxPrice,
            String sort
    ) {
        List<Product> list = productRepository.findAll();

        if (q != null && !q.isBlank()) {
            String qq = q.toLowerCase();
            list = list.stream()
                    .filter(p ->
                            p.getTitle().toLowerCase().contains(qq) ||
                                    (p.getBrand() != null && p.getBrand().toLowerCase().contains(qq)) ||
                                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(qq))
                    )
                    .toList();
        }

        if (category != null && !category.isBlank()) {
            list = list.stream()
                    .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                    .toList();
        }

        if (brand != null && !brand.isBlank()) {
            list = list.stream()
                    .filter(p -> brand.equalsIgnoreCase(p.getBrand()))
                    .toList();
        }

        if (maxPrice != null) {
            list = list.stream()
                    .filter(p -> p.getPrice() <= maxPrice)
                    .toList();
        }

        if ("price_asc".equals(sort)) {
            list = list.stream()
                    .sorted(Comparator.comparing(Product::getPrice))
                    .toList();
        } else if ("price_desc".equals(sort)) {
            list = list.stream()
                    .sorted(Comparator.comparing(Product::getPrice).reversed())
                    .toList();
        } else if ("popular".equals(sort)) {
            list = list.stream()
                    .sorted(Comparator.comparing(Product::getSalesCount).reversed())
                    .toList();
        }

        return list;
    }

    public List<Product> getPopular() {
        return productRepository.findTop10ByOrderBySalesCountDesc();
    }

    public List<Product> getDiscounts() {
        return productRepository.findTop10ByDiscountTrueOrderBySalesCountDesc();
    }

    public List<Product> getRandom() {
        return productRepository.findRandom10();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product p) {
        if (!p.isDiscount()) {
            p.setOldPrice(null);
        } else if (p.getOldPrice() == null) {
            p.setOldPrice(BigDecimal.valueOf(p.getPrice()));
        }
        return productRepository.save(p);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}