package com.pdau.Authentication.repository;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByCorreo(String correo);
    List<Admin> findByRol(Rol rol);
}
