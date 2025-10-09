package org.example.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.model.PasswordResetToken;
import org.example.model.UserEntity;
import org.example.repository.PasswordResetTokenRepository;
import org.example.repository.UserRepository;
import org.example.service.EmailService;
import org.example.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            boolean success = passwordResetService.sendResetToken(email);
            if (!success) {
                model.addAttribute("error", "No user found with that email address.");
            } else {
                model.addAttribute("message", "Reset code has been sent to your email.");
            }
        } catch (MessagingException e) {
            model.addAttribute("error", "Failed to send reset email.");
            e.printStackTrace();
        }

        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleReset(
            @RequestParam String email,
            @RequestParam String token,
            @RequestParam String newPassword,
            Model model
    ) {
        boolean success = passwordResetService.resetPassword(email, token, newPassword);
        if (!success) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "reset-password";
        }

        return "redirect:/login?resetSuccess";
    }
}