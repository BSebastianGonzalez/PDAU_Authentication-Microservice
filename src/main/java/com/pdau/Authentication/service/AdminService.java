package com.pdau.Authentication.service;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin getAdmin(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
    }

    public Admin createAdmin(Admin admin) {
        admin.setContrasenia(passwordEncoder.encode(admin.getContrasenia()));
        return adminRepository.save(admin);
    }

    public Admin updateAdmin(Long id, Admin admin) {
        Admin existing = getAdmin(id);

        if (admin.getCedula() != null && !admin.getCedula().isEmpty()) {
            existing.setCedula(admin.getCedula());
        }
        if (admin.getNombre() != null && !admin.getNombre().isEmpty()) {
            existing.setNombre(admin.getNombre());
        }
        if (admin.getApellido() != null && !admin.getApellido().isEmpty()) {
            existing.setApellido(admin.getApellido());
        }
        if (admin.getTelefono() != null) {
            existing.setTelefono(admin.getTelefono());
        }
        if (admin.getDireccion() != null && !admin.getDireccion().isEmpty()) {
            existing.setDireccion(admin.getDireccion());
        }
        if (admin.getCorreo() != null && !admin.getCorreo().isEmpty()) {
            existing.setCorreo(admin.getCorreo());
        }
        if (admin.getContrasenia() != null && !admin.getContrasenia().isEmpty()) {
            existing.setContrasenia(passwordEncoder.encode(admin.getContrasenia()));
        }
        if (admin.getRol() != null) {
            existing.setRol(admin.getRol());
        }

        return adminRepository.save(existing);
    }
}
