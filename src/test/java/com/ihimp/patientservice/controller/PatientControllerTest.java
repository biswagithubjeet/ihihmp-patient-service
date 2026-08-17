package com.ihimp.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihimp.patientservice.constant.PatientConstants;
import com.ihimp.patientservice.dto.request.AddressRequest;
import com.ihimp.patientservice.dto.request.AddressUpdateRequest;
import com.ihimp.patientservice.dto.request.EmergencyContactRequest;
import com.ihimp.patientservice.dto.request.EmergencyContactUpdateRequest;
import com.ihimp.patientservice.dto.request.IdentityDocumentRequest;
import com.ihimp.patientservice.dto.request.IdentityDocumentUpdateRequest;
import com.ihimp.patientservice.dto.request.PatientRequest;
import com.ihimp.patientservice.dto.request.PatientUpdateRequest;
import com.ihimp.patientservice.dto.response.PatientResponse;
import com.ihimp.patientservice.enums.AddressType;
import com.ihimp.patientservice.enums.BloodGroup;
import com.ihimp.patientservice.enums.DocumentType;
import com.ihimp.patientservice.enums.Gender;
import com.ihimp.patientservice.enums.MaritalStatus;
import com.ihimp.patientservice.enums.RelationshipType;
import com.ihimp.patientservice.exception.DuplicateResourceException;
import com.ihimp.patientservice.exception.ResourceNotFoundException;
import com.ihimp.patientservice.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class,
        excludeAutoConfiguration =
                OAuth2ResourceServerWebSecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    // ============================================================
    // POST /api/v1/patients
    // ============================================================

    @Test
    void registerPatient_shouldReturn201WhenRequestIsValid()
            throws Exception {

        PatientRequest request = createValidPatientRequest();

        PatientResponse response = new PatientResponse();

        when(patientService.registerPatient(any(PatientRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );

        verify(patientService)
                .registerPatient(any(PatientRequest.class));
    }


    @Test
    void registerPatient_shouldReturn400WhenRequestIsInvalid()
            throws Exception {

        PatientRequest request = new PatientRequest();

        mockMvc.perform(
                        post("/api/v1/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }


    @Test
    void registerPatient_shouldReturn409WhenPatientAlreadyExists()
            throws Exception {

        PatientRequest request = createValidPatientRequest();

        when(patientService.registerPatient(any(PatientRequest.class)))
                .thenThrow(
                        new DuplicateResourceException(
                                "Patient already exists"
                        )
                );

        mockMvc.perform(
                        post("/api/v1/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict());

        verify(patientService)
                .registerPatient(any(PatientRequest.class));
    }


    // ============================================================
    // GET /api/v1/patients/{patientId}
    // ============================================================

    @Test
    void getPatientByPatientId_shouldReturn200WhenPatientExists()
            throws Exception {

        String patientId = "PAT-100001";

        PatientResponse response =
                new PatientResponse();

        when(patientService.getPatientByPatientId(patientId))
                .thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );

        verify(patientService)
                .getPatientByPatientId(patientId);
    }


    @Test
    void getPatientByPatientId_shouldReturn404WhenPatientDoesNotExist()
            throws Exception {

        String patientId = "PAT-999999";

        when(patientService.getPatientByPatientId(patientId))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Patient not found: " + patientId
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                )
                .andExpect(status().isNotFound());

        verify(patientService)
                .getPatientByPatientId(patientId);
    }


    // ============================================================
    // GET /api/v1/patients
    // ============================================================

    @Test
    void getAllPatients_shouldReturn200()
            throws Exception {

        PatientResponse response =
                new PatientResponse();

        PageImpl<PatientResponse> page =
                new PageImpl<>(List.of(response));

        when(patientService.getAllPatients(0, 10))
                .thenReturn(page);

        mockMvc.perform(
                        get("/api/v1/patients")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );

        verify(patientService)
                .getAllPatients(0, 10);
    }


    @Test
    void getAllPatients_shouldUseDefaultPagination()
            throws Exception {

        PageImpl<PatientResponse> page =
                new PageImpl<>(List.of());

        when(patientService.getAllPatients(0, 10))
                .thenReturn(page);

        mockMvc.perform(
                        get("/api/v1/patients")
                )
                .andExpect(status().isOk());

        verify(patientService).getAllPatients(0, 20);
    }


    @Test
    void getAllPatients_shouldReturn400WhenPageIsNegative()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/patients")
                                .param("page", "-1")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }


    @Test
    void getAllPatients_shouldReturn400WhenPageSizeIsZero()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/patients")
                                .param("page", "0")
                                .param("size", "0")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }


    @Test
    void getAllPatients_shouldReturn400WhenPageSizeExceedsMaximum()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/patients")
                                .param("page", "0")
                                .param("size", "101")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }


    // ============================================================
    // PUT /api/v1/patients/{patientId}
    // ============================================================

    @Test
    void updatePatient_shouldReturn200WhenRequestIsValid()
            throws Exception {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                createValidPatientUpdateRequest();

        PatientResponse response =
                new PatientResponse();

        when(
                patientService.updatePatient(
                        eq(patientId),
                        any(PatientUpdateRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );

        verify(patientService)
                .updatePatient(
                        eq(patientId),
                        any(PatientUpdateRequest.class)
                );
    }


    @Test
    void updatePatient_shouldReturn400WhenRequestIsInvalid()
            throws Exception {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                new PatientUpdateRequest();

        mockMvc.perform(
                        put(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }


    @Test
    void updatePatient_shouldReturn404WhenPatientDoesNotExist()
            throws Exception {

        String patientId = "PAT-999999";

        PatientUpdateRequest request =
                createValidPatientUpdateRequest();

        when(
                patientService.updatePatient(
                        eq(patientId),
                        any(PatientUpdateRequest.class)
                )
        ).thenThrow(
                new ResourceNotFoundException(
                        "Patient not found: " + patientId
                )
        );

        mockMvc.perform(
                        put(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound());

        verify(patientService)
                .updatePatient(
                        eq(patientId),
                        any(PatientUpdateRequest.class)
                );
    }


    @Test
    void updatePatient_shouldReturn409WhenDuplicateInformationExists()
            throws Exception {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                createValidPatientUpdateRequest();

        when(
                patientService.updatePatient(
                        eq(patientId),
                        any(PatientUpdateRequest.class)
                )
        ).thenThrow(
                new DuplicateResourceException(
                        "Duplicate patient information"
                )
        );

        mockMvc.perform(
                        put(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict());

        verify(patientService)
                .updatePatient(
                        eq(patientId),
                        any(PatientUpdateRequest.class)
                );
    }


    // ============================================================
    // DELETE /api/v1/patients/{patientId}
    // ============================================================

    @Test
    void deletePatient_shouldReturn204WhenPatientIsDeleted()
            throws Exception {

        String patientId = "PAT-100001";

        doNothing()
                .when(patientService)
                .deletePatient(patientId);

        mockMvc.perform(
                        delete(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                )
                .andExpect(status().isNoContent());

        verify(patientService)
                .deletePatient(patientId);
    }


    @Test
    void deletePatient_shouldReturn404WhenPatientDoesNotExist()
            throws Exception {

        String patientId = "PAT-999999";

        doThrow(
                new ResourceNotFoundException(
                        "Patient not found: " + patientId
                )
        )
                .when(patientService)
                .deletePatient(patientId);

        mockMvc.perform(
                        delete(
                                "/api/v1/patients/{patientId}",
                                patientId
                        )
                )
                .andExpect(status().isNotFound());

        verify(patientService)
                .deletePatient(patientId);
    }


    // ============================================================
    // VALID CREATE REQUEST
    // ============================================================

    private PatientRequest createValidPatientRequest() {

        PatientRequest request =
                new PatientRequest();

        request.setFirstName("John");
        request.setMiddleName("Michael");
        request.setLastName("Doe");

        request.setDateOfBirth(
                LocalDate.of(1990, 1, 1)
        );

        request.setGender(
                Gender.values()[0]
        );

        request.setBloodGroup(
                BloodGroup.values()[0]
        );

        request.setMaritalStatus(
                MaritalStatus.values()[0]
        );

        request.setEmail("john.doe@test.com");

        request.setCountryCode("+91");

        request.setPhoneNumber("9876543210");

        request.setAlternatePhoneNumber("9876543211");

        request.setNationality("Indian");

        request.setProfilePhotoUrl(
                "https://example.com/profile.jpg"
        );

        request.setAddresses(
                List.of(
                        createValidAddressRequest()
                )
        );

        request.setEmergencyContacts(
                List.of(
                        createValidEmergencyContactRequest()
                )
        );

        request.setIdentityDocuments(
                List.of(
                        createValidIdentityDocumentRequest()
                )
        );

        return request;
    }


    // ============================================================
    // VALID UPDATE REQUEST
    // ============================================================

    private PatientUpdateRequest createValidPatientUpdateRequest() {

        PatientUpdateRequest request =
                new PatientUpdateRequest();

        request.setFirstName("John");
        request.setMiddleName("Michael");
        request.setLastName("Doe");

        request.setDateOfBirth(
                LocalDate.of(1990, 1, 1)
        );

        request.setGender(
                Gender.values()[0]
        );

        request.setBloodGroup(
                BloodGroup.values()[0]
        );

        request.setMaritalStatus(
                MaritalStatus.values()[0]
        );

        request.setEmail("john.doe@test.com");

        request.setCountryCode("+91");

        request.setPhoneNumber("9876543210");

        request.setAlternatePhoneNumber("9876543211");

        request.setNationality("Indian");

        request.setProfilePhotoUrl(
                "https://example.com/profile.jpg"
        );

        request.setAddresses(
                List.of(
                        createValidAddressUpdateRequest()
                )
        );

        request.setEmergencyContacts(
                List.of(
                        createValidEmergencyContactUpdateRequest()
                )
        );

        request.setIdentityDocuments(
                List.of(
                        createValidIdentityDocumentUpdateRequest()
                )
        );

        return request;
    }


    // ============================================================
    // VALID ADDRESS REQUEST
    // ============================================================

    private AddressRequest createValidAddressRequest() {

        AddressRequest request =
                new AddressRequest();

        request.setAddressType(
                AddressType.values()[0]
        );

        request.setAddressLine1(
                "123 Main Street"
        );

        request.setAddressLine2(
                "Apartment 101"
        );

        request.setCity(
                "Bhubaneswar"
        );

        request.setDistrict(
                "Khordha"
        );

        request.setState(
                "Odisha"
        );

        request.setCountry(
                "India"
        );

        request.setPostalCode(
                "751001"
        );

        request.setLandmark(
                "Near Main Market"
        );

        request.setPrimaryAddress(true);

        return request;
    }


    // ============================================================
    // VALID ADDRESS UPDATE REQUEST
    // ============================================================

    private AddressUpdateRequest createValidAddressUpdateRequest() {

        AddressUpdateRequest request =
                new AddressUpdateRequest();

        request.setId(1L);

        request.setAddressType(
                AddressType.values()[0]
        );

        request.setAddressLine1(
                "123 Main Street"
        );

        request.setAddressLine2(
                "Apartment 101"
        );

        request.setCity(
                "Bhubaneswar"
        );

        request.setDistrict(
                "Khordha"
        );

        request.setState(
                "Odisha"
        );

        request.setCountry(
                "India"
        );

        request.setPostalCode(
                "751001"
        );

        request.setLandmark(
                "Near Main Market"
        );

        request.setPrimaryAddress(true);

        return request;
    }


    // ============================================================
    // VALID EMERGENCY CONTACT REQUEST
    // ============================================================

    private EmergencyContactRequest createValidEmergencyContactRequest() {

        EmergencyContactRequest request =
                new EmergencyContactRequest();

        request.setFirstName("Jane");

        request.setLastName("Doe");

        request.setRelationshipType(
                RelationshipType.values()[0]
        );

        request.setCountryCode("+91");

        request.setMobileNumber(
                "9876543211"
        );

        request.setAlternateMobileNumber(
                "9876543212"
        );

        request.setEmail(
                "jane.doe@test.com"
        );

        request.setPrimaryContact(true);

        return request;
    }


    // ============================================================
    // VALID EMERGENCY CONTACT UPDATE REQUEST
    // ============================================================

    private EmergencyContactUpdateRequest
    createValidEmergencyContactUpdateRequest() {

        EmergencyContactUpdateRequest request =
                new EmergencyContactUpdateRequest();

        request.setId(1L);

        request.setFirstName("Jane");

        request.setLastName("Doe");

        request.setRelationshipType(
                RelationshipType.values()[0]
        );

        request.setCountryCode("+91");

        request.setMobileNumber(
                "9876543211"
        );

        request.setAlternateMobileNumber(
                "9876543212"
        );

        request.setEmail(
                "jane.doe@test.com"
        );

        request.setPrimaryContact(true);

        return request;
    }


    // ============================================================
    // VALID IDENTITY DOCUMENT REQUEST
    // ============================================================

    private IdentityDocumentRequest
    createValidIdentityDocumentRequest() {

        IdentityDocumentRequest request =
                new IdentityDocumentRequest();

        request.setDocumentType(
                DocumentType.values()[0]
        );

        request.setDocumentNumber(
                "DOC-123456"
        );

        request.setIssuedBy(
                "Government Authority"
        );

        request.setIssueDate(
                LocalDate.of(2020, 1, 1)
        );

        request.setExpiryDate(
                LocalDate.of(2030, 1, 1)
        );

        return request;
    }


    // ============================================================
    // VALID IDENTITY DOCUMENT UPDATE REQUEST
    // ============================================================

    private IdentityDocumentUpdateRequest
    createValidIdentityDocumentUpdateRequest() {

        IdentityDocumentUpdateRequest request =
                new IdentityDocumentUpdateRequest();

        request.setId(1L);

        request.setDocumentType(
                DocumentType.values()[0]
        );

        request.setDocumentNumber(
                "DOC-123456"
        );

        request.setIssuedBy(
                "Government Authority"
        );

        request.setIssueDate(
                LocalDate.of(2020, 1, 1)
        );

        request.setExpiryDate(
                LocalDate.of(2030, 1, 1)
        );

        return request;
    }
}

