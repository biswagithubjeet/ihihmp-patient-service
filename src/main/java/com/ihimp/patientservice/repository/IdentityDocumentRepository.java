package com.ihimp.patientservice.repository;

import com.ihimp.patientservice.entity.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IdentityDocumentRepository extends JpaRepository<IdentityDocument,Long> {

    Optional<IdentityDocument> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumberAndPatientIdNot(String documentNumber, Long id);

    @Query("""
        SELECT d.documentNumber
        FROM IdentityDocument d
        WHERE d.documentNumber IN :documentNumbers
        """)
    List<String> findExistingDocumentNumbers(
            @Param("documentNumbers") Collection<String> documentNumbers);
}
