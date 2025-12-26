package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final ProductService productService;

    @GetMapping("/catalog")
    public String catalog(
            Model model,
            Principal principal,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "relevance") String sort
    ) {

        model.addAttribute("username",
                principal != null ? principal.getName() : "Гость");

        model.addAttribute("pageTitle", "Каталог товаров");

        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("brand", brand);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);

        model.addAttribute(
                "products",
                productService.filter(q, category, brand, maxPrice, sort)
        );

        model.addAttribute("view", "catalog");
        return "layout";
    }
}