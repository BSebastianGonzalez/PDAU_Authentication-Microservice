package com.pdau.Authentication.service;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.model.Documento;
import com.pdau.Authentication.model.Rol;
import com.pdau.Authentication.model.TipoDocumento;
import com.pdau.Authentication.repository.AdminRepository;
import com.pdau.Authentication.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final DocumentoRepository documentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${documentos.max-size}")
    private long maxFileSize;

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

    public List<String> obtenerCorreosAdminsEspeciales() {
        return adminRepository.findByRol(Rol.ADMIN_ESPECIAL)
                .stream()
                .map(Admin::getCorreo)
                .collect(Collectors.toList());
    }

    public Documento subirDocumento(Long adminId, MultipartFile file, TipoDocumento tipo) throws Exception {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador no encontrado con ID: " + adminId));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe adjuntar un archivo.");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (2 MB).");
        }

        Documento documento = new Documento();
        documento.setNombre(file.getOriginalFilename());
        documento.setTipoContenido(file.getContentType());
        documento.setDatos(file.getBytes());
        documento.setTipoDocumento(tipo);
        documento.setAdmin(admin);

        // Guardar documento
        Documento saved = documentoRepository.save(documento);

        // Agregarlo al admin
        admin.getDocumentos().add(saved);
        adminRepository.save(admin);

        return saved;
    }

    public Documento obtenerDocumentoPorTipo(Long adminId, TipoDocumento tipo) {
        return documentoRepository.findByAdminIdAndTipoDocumento(adminId, tipo)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado para el tipo " + tipo));
    }
}
