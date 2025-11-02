package com.pdau.Authentication.controller;

import com.pdau.Authentication.dto.AuthRequest;
import com.pdau.Authentication.dto.AuthResponse;
import com.pdau.Authentication.dto.ResetPasswordRequest;
import com.pdau.Authentication.service.AuthService;
import com.pdau.Authentication.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request.getCorreo(), request.getContrasenia()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        passwordResetService.sendResetLink(correo);
        return ResponseEntity.ok("Se ha enviado un enlace de recuperación al correo.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("La contraseña ha sido cambiada correctamente.");
    }
}
