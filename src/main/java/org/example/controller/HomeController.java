package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/home")
    public String home(Model model, Principal principal) {

        model.addAttribute("username",
                principal != null ? principal.getName() : "Гость");

        model.addAttribute("pageTitle", "YOStore — Главная");

        model.addAttribute("popular", productService.getPopular());
        model.addAttribute("discounts", productService.getDiscounts());
        model.addAttribute("random", productService.getRandom());

        model.addAttribute("view", "home");
        return "layout";
    }
}