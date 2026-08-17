package com.ihimp.patientservice.validator;

import com.ihimp.patientservice.constant.PatientConstants;
import com.ihimp.patientservice.dto.request.PatientRequest;
import com.ihimp.patientservice.dto.request.PatientUpdateRequest;
import com.ihimp.patientservice.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PatientValidator {

    public void validateForCreate(PatientRequest request){

        validateDateOfBirth(request.getDateOfBirth());
    }

    public void validateForUpdate(PatientUpdateRequest request){

        validateDateOfBirth(request.getDateOfBirth());
    }

    private void validateDateOfBirth(LocalDate dateOfBirth) {

        if(dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException(
                    PatientConstants.DATE_OF_BIRTH_FUTURE_MESSAGE
            );
        }
    }
}
