package com.pdau.Authentication.repository;

import com.pdau.Authentication.model.Documento;
import com.pdau.Authentication.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    Optional<Documento> findByAdminIdAndTipoDocumento(Long adminId, TipoDocumento tipoDocumento);
}
