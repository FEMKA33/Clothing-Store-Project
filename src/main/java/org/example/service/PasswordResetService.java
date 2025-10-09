package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.PasswordResetToken;
import org.example.model.UserEntity;
import org.example.repository.PasswordResetTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public boolean sendResetToken(String email) throws jakarta.mail.MessagingException {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        String token = UUID.randomUUID().toString().substring(0, 6);
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);

        tokenRepository.deleteByEmail(email); // удалить старые токены

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpirationTime(expiration);
        tokenRepository.save(resetToken);

        emailService.sendResetToken(email, token);
        return true;
    }

    public boolean resetPassword(String email, String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmail(email);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (!resetToken.getToken().equals(token)) {
            return false;
        }

        if (resetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        return true;
    }
}