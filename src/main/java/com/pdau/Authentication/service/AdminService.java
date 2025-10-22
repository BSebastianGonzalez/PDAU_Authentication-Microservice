package com.pdau.Authentication.service;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    public Admin createAdmin(Admin admin) {
        admin.setContrasenia(passwordEncoder.encode(admin.getContrasenia()));
        return adminRepository.save(admin);
    }
}
