package com.ihimp.patientservice.validator;

import com.ihimp.patientservice.constant.PatientConstants;
import com.ihimp.patientservice.exception.BadRequestException;

import java.time.LocalDate;

public class IdentityDocumentValidator {

    public void validate(LocalDate issueDate, LocalDate expiryDate){

        if (expiryDate != null && issueDate != null && expiryDate.isBefore(issueDate)){

            throw new BadRequestException(PatientConstants.IDENTITY_DOCUMENT_EXPIRY_MESSAGE);
        }
    }
}
