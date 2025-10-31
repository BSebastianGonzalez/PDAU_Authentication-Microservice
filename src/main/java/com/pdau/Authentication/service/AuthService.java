package com.pdau.Authentication.service;

import com.pdau.Authentication.dto.AuthResponse;
import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(String correo, String contrasenia) {
        Admin admin = adminRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasenia, admin.getContrasenia())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(admin);
        return new AuthResponse(admin.getId(), token, admin.getRol().name());
    }
}
