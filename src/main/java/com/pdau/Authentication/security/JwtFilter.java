package com.pdau.Authentication.security;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.repository.AdminRepository;
import com.pdau.Authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener el encabezado Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String correo;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token sin el prefijo "Bearer "
        jwt = authHeader.substring(7);
        correo = jwtService.extractClaims(jwt).getSubject();

        if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Admin admin = adminRepository.findByCorreo(correo).orElse(null);

            if (admin != null) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(admin, null, null);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // Excluir rutas públicas (no requieren JWT)
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/forgot-password")
                || path.startsWith("/auth/reset-password");
    }
}

