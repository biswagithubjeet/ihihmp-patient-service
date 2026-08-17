package com.ihimp.patientservice.dto.response;

import com.ihimp.patientservice.enums.DocumentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDocumentResponse {

    private Long id;
    private DocumentType documentType;
    private String documentNumber;
    private String issuedBy;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Boolean verified;
    private Boolean active;
}
