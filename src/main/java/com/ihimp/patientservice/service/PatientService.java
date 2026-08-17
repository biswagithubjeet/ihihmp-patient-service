package com.ihimp.patientservice.service;

import com.ihimp.patientservice.dto.request.PatientRequest;
import com.ihimp.patientservice.dto.request.PatientUpdateRequest;
import com.ihimp.patientservice.dto.response.PatientResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {

    /*Register a new Patient*/
    PatientResponse registerPatient(PatientRequest request);

    /*Search patient*/
    PatientResponse getPatientByPatientId(String patientId);

    /*List all patient*/
    Page<PatientResponse> getAllPatients(int page, int size);

    /*Update patient*/
    PatientResponse updatePatient(String patientId, PatientUpdateRequest request);

    /*Soft delete (mark inactive)*/
    void deletePatient(String patientId);
}
