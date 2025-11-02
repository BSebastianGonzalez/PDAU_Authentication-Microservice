package com.pdau.Authentication.service;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.model.PasswordResetToken;
import com.pdau.Authentication.repository.AdminRepository;
import com.pdau.Authentication.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;

    public void sendResetLink(String correo) {
        Admin admin = adminRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Correo no registrado"));

        // Generar token único
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, admin, LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        // Crear enlace
        String link = "http://localhost:5173/reset-password?token=" + token;

        // Enviar correo
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(correo);
        mail.setSubject("Restablecimiento de contraseña");
        mail.setText("Haz clic en el siguiente enlace para cambiar tu contraseña:\n" + link);
        mailSender.send(mail);
        System.out.println(mail);
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

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(admin.getCorreo());
        mail.setSubject("Contraseña actualizada");
        mail.setText("Haz clic en el siguiente enlace para cambiar tu contraseña:\n");
        mailSender.send(mail);
    }
}

