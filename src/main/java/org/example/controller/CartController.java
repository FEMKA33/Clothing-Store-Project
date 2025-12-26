package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.CartItem;
import org.example.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        List<CartItem> items = cartService.getCart(principal.getName());
        double total = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getProduct().getPrice())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("pageTitle", "Корзина — YOStore");
        model.addAttribute("view", "cart");
        return "layout";
    }

    @PostMapping("/checkout")
    public String goToCheckout(Principal principal, Model model) {
        List<CartItem> items = cartService.getCart(principal.getName());
        if (items.isEmpty()) {
            model.addAttribute("error", "Корзина пуста!");
            return "redirect:/cart";
        }

        double total = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getProduct().getPrice())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("pageTitle", "Оформление заказа");
        model.addAttribute("view", "checkout");
        return "layout";
    }

    @PostMapping("/clear")
    public String clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return "redirect:/cart";
    }

    @PostMapping("/confirm")
    public String confirmOrder(Principal principal) {
        cartService.checkout(principal.getName());
        return "redirect:/home?success";
    }
}