package com.pdau.Authentication.service;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.model.PasswordResetToken;
import com.pdau.Authentication.repository.AdminRepository;
import com.pdau.Authentication.repository.PasswordResetTokenRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;
    private final TemplateEngine templateEngine;

    public void sendResetLink(String correo) {
        Admin admin = adminRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Correo no registrado"));

        // Generar token único con expiración
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token, admin, LocalDateTime.now().plusMinutes(15)
        );
        tokenRepository.save(resetToken);

        // Crear enlace de restablecimiento
        String link = "https://micro-pdau.vercel.app/reset-password?token=" + token;

        // Renderizar plantilla HTML
        Context context = new Context();
        context.setVariable("nombre", admin.getNombre());
        context.setVariable("link", link);

        String htmlContent = templateEngine.process("reset-password-email", context);

        // Enviar correo HTML
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(correo);
            helper.setSubject("Restablecimiento de contraseña");
            helper.setText(htmlContent, true); // true => HTML
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        Admin admin = resetToken.getAdmin();
        admin.setContrasenia(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);

        // Invalida el token
        tokenRepository.delete(resetToken);

        // Renderizar plantilla de confirmación
        Context context = new Context();
        context.setVariable("nombre", admin.getNombre());

        String htmlContent = templateEngine.process("password-updated-email", context);

        // Enviar correo HTML
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(admin.getCorreo());
            helper.setSubject("Contraseña actualizada");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }
}

