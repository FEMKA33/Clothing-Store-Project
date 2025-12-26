package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String brand;
    private String category;
    private Double price;
    @Column(length = 2048)
    private String description;
    private String imageUrl;

    private int views;
    private int salesCount;

    private boolean discount;
    private BigDecimal oldPrice;

    @Transient
    private boolean inFavorites;
}