package com.ihimp.patientservice.dto.request;

import com.ihimp.patientservice.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocumentUpdateRequest {

    private Long id;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document number is required")
    @Size(max = 100, message = "Document number cannot exceed 100 characters")
    private String documentNumber;

    @NotBlank(message = "Issued by is required")
    @Size(max = 150, message = "Issued by cannot exceed 150 characters")
    private String issuedBy;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    private LocalDate expiryDate;
}
