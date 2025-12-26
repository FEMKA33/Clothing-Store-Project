package org.example.config;

import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initProducts(ProductRepository repo){
        return args -> {
            if (repo.count() == 0) {
                repo.save(Product.builder()
                        .title("Классическая белая футболка")
                        .brand("Zara")
                        .category("Одежда")
                        .price(799.0)
                        .imageUrl("https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&q=80")
                        .description("Удобная хлопковая футболка — базовый элемент гардероба.")
                        .build());

                repo.save(Product.builder()
                        .title("Черные кроссовки")
                        .brand("Nike")
                        .category("Обувь")
                        .price(4999.0)
                        .imageUrl("https://images.unsplash.com/photo-1520975918318-3e48e7f1e8e2?w=900&q=80")
                        .description("Стильные и комфортные кроссовки для города.")
                        .build());

                repo.save(Product.builder()
                        .title("Сумка через плечо")
                        .brand("Adidas")
                        .category("Аксессуары")
                        .price(2499.0)
                        .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=900&q=80")
                        .description("Компактная сумка для важных мелочей.")
                        .build());
            }
        };
    }
}