package com.ihimp.patientservice.controller;

import com.ihimp.patientservice.constant.PatientConstants;
import com.ihimp.patientservice.dto.request.PatientRequest;
import com.ihimp.patientservice.dto.request.PatientUpdateRequest;
import com.ihimp.patientservice.dto.response.PatientResponse;
import com.ihimp.patientservice.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Patient Management",
        description = "APIs for patient registration and management"
)
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {

    private final PatientService patientService;
    //Register patient
    @Operation(
            summary = "Register a new patient",
            description = "Creates a new patient with addresses, emergency contacts and identity documents"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "409", description = "Patient or identity document already exists")
    })
    @PostMapping
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRequest request){

        PatientResponse response = patientService.registerPatient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    //Get patient
    @Operation(
            summary = "Get patient by patient ID",
            description = "Retrieves complete patient information using the patient ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientByPatientId(@Valid @PathVariable String patientId){
        PatientResponse response = patientService.getPatientByPatientId(patientId);
        return ResponseEntity.ok(response);
    }
    //List of patients
    @GetMapping
    @Operation(
            summary = "Get all patients",
            description = "Returns a paginated list of patients"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patients retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(
                    defaultValue = PatientConstants.DEFAULT_PAGE_NUMBER
            )
            @Min(value = PatientConstants.MIN_PAGE_NUMBER, message = "Page number cannot be negative")
            int page,

            @RequestParam(
                    defaultValue = PatientConstants.DEFAULT_PAGE_SIZE
            )
            @Min(value = PatientConstants.MIN_PAGE_SIZE, message = "Page size must be at least 1")
            @Max(value = PatientConstants.MAX_PAGE_SIZE, message = "Page size cannot exceed 100")
            int size){

        Page<PatientResponse> response = patientService.getAllPatients(page, size);

        return ResponseEntity.ok(response);
    }
    //Update patient
    @Operation(
            summary = "Update patient",
            description = "Updates patient information and synchronizes related records"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate information")
    })
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable String patientId,
            @Valid @RequestBody PatientUpdateRequest request){

        PatientResponse response = patientService.updatePatient(patientId, request);
        return ResponseEntity.ok(response);
    }
    //Soft delete
    @Operation(
            summary = "Deactivate patient",
            description = "Soft deletes a patient by marking the patient and related records inactive"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Patient deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{patientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable String patientId){
        patientService.deletePatient(patientId);
    }
}
