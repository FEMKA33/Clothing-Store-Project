package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.model.UserEntity;
import org.example.model.UserRegistrationDto;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDto registrationDto,
                               BindingResult result, Model model) {
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists");
        }

        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Email already registered");
        }

        if (result.hasErrors()) {
            return "register";
        }

        UserEntity user = UserEntity.builder()
                .username(registrationDto.getUsername())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .email(registrationDto.getEmail())
                .role("USER")
                .build();

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}