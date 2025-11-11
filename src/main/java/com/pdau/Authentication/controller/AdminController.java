package com.pdau.Authentication.controller;

import com.pdau.Authentication.model.Admin;
import com.pdau.Authentication.model.Documento;
import com.pdau.Authentication.model.TipoDocumento;
import com.pdau.Authentication.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin created = adminService.createAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdmin(@PathVariable Long id) {
        Admin admin = adminService.getAdmin(id);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        Admin updated = adminService.updateAdmin(id, admin);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/especiales/correos")
    public ResponseEntity<List<String>> getCorreosAdminsEspeciales() {
        return ResponseEntity.ok(adminService.obtenerCorreosAdminsEspeciales());
    }

    @PostMapping("/{id}/documento")
    public ResponseEntity<?> subirDocumento(
            @PathVariable("id") Long adminId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("tipo") TipoDocumento tipo
    ) {
        try {
            Documento documento = adminService.subirDocumento(adminId, file, tipo);
            return ResponseEntity.ok(documento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el documento: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/documento")
    public ResponseEntity<?> obtenerDocumento(
            @PathVariable("id") Long adminId,
            @RequestParam("tipo") TipoDocumento tipo
    ) {
        try {
            Documento documento = adminService.obtenerDocumentoPorTipo(adminId, tipo);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(documento.getTipoContenido()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNombre() + "\"")
                    .body(documento.getDatos());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el documento: " + e.getMessage());
        }
    }
}
