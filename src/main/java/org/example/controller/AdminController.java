package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("pageTitle", "Админ-панель");
        model.addAttribute("products", productService.findAll());
        model.addAttribute("view", "admin/admin");
        return "layout";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("pageTitle", "Добавить товар");
        model.addAttribute("product", new Product());
        model.addAttribute("view", "admin/add");
        return "layout";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Редактировать товар");
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("view", "admin/edit");
        return "layout";
    }

    @PostMapping("/edit")
    public String editProduct(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin";
    }
}