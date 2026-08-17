package com.ihimp.patientservice.entity;

import com.ihimp.patientservice.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "identity_documents",
        uniqueConstraints = {

                @UniqueConstraint(
                        name = "uk_document_number",
                        columnNames = "document_number"
                )
        },
        indexes = {
                @Index(name = "idx_patient_id", columnList = "patient_id"),
                @Index(name = "idx_document_type", columnList = "document_type"),
                @Index(name = "idx_document_number", columnList = "document_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocument extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "document_number",nullable = false, length = 100)
    private String documentNumber;

    @Column(name = "issued_By", nullable = false, length = 150)
    private String issued_by;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_identity_document_patient")
    )
    private Patient patient;
}
