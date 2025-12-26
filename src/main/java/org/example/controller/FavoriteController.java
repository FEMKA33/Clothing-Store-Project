package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Product;
import org.example.model.UserEntity;
import org.example.service.FavoriteService;
import org.example.service.ProductService;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ProductService productService;
    private final UserService userService;

    @PostMapping("/toggle/{productId}")
    @ResponseBody
    public boolean toggle(@PathVariable Long productId, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());
        Product product = productService.findById(productId);
        return favoriteService.toggle(user, product);
    }

    @GetMapping
    public String favorites(Model model, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());

        List<Product> products = favoriteService.getFavorites(user);
        List<Long> favoriteIds = favoriteService.getFavoriteProductIds(user);

        model.addAttribute("products", products);
        model.addAttribute("favoriteIds", favoriteIds);
        model.addAttribute("pageTitle", "Избранное");
        model.addAttribute("view", "favorites");

        return "layout";
    }
}