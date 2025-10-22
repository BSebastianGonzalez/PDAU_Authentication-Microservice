package com.pdau.Authentication.repository;

import com.pdau.Authentication.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByCorreo(String correo);
}
