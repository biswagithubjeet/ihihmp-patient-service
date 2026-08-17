package com.ihimp.patientservice.service;


import com.ihimp.patientservice.dto.request.*;
import com.ihimp.patientservice.dto.response.PatientResponse;
import com.ihimp.patientservice.entity.Address;
import com.ihimp.patientservice.entity.EmergencyContact;
import com.ihimp.patientservice.entity.IdentityDocument;
import com.ihimp.patientservice.entity.Patient;
import com.ihimp.patientservice.enums.PatientStatus;
import com.ihimp.patientservice.exception.BadRequestException;
import com.ihimp.patientservice.exception.DuplicateResourceException;
import com.ihimp.patientservice.exception.ResourceNotFoundException;
import com.ihimp.patientservice.generator.PatientIdGenerator;
import com.ihimp.patientservice.mapper.AddressMapper;
import com.ihimp.patientservice.mapper.EmergencyContactMapper;
import com.ihimp.patientservice.mapper.IdentityDocumentMapper;
import com.ihimp.patientservice.mapper.PatientMapper;
import com.ihimp.patientservice.repository.AddressRepository;
import com.ihimp.patientservice.repository.EmergencyContactRepository;
import com.ihimp.patientservice.repository.IdentityDocumentRepository;
import com.ihimp.patientservice.repository.PatientRepository;
import com.ihimp.patientservice.service.impl.PatientServiceImpl;
import com.ihimp.patientservice.validator.PatientValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private EmergencyContactRepository emergencyContactRepository;

    @Mock
    private IdentityDocumentRepository identityDocumentRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private EmergencyContactMapper emergencyContactMapper;

    @Mock
    private IdentityDocumentMapper identityDocumentMapper;

    @Mock
    private PatientIdGenerator patientIdGenerator;

    @Mock
    private PatientValidator patientValidator;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;

    @BeforeEach
    void setUp(){
        patient = new Patient();
        patient.setPatientId("PAT-100001");
        patient.setEmail("patient@test.com");
        patient.setStatus(PatientStatus.ACTIVE);
    }

         /*REGISTER PATIENT*/
    @Test
    void registerPatient_shouldRegisterPatientSuccessfully() {

        PatientRequest request = mock(PatientRequest.class);
        PatientResponse response = mock(PatientResponse.class);

        when(request.getEmail()).thenReturn("patient@test.com");
        when(request.getIdentityDocuments()).thenReturn(List.of());
        when(patientRepository.existsByEmail("patient@test.com")).thenReturn(false);
        when(identityDocumentRepository.findExistingDocumentNumbers(anySet())).thenReturn(List.of());
        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientIdGenerator.generate()).thenReturn("PAT-100001");
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse result = patientService.registerPatient(request);

        assertNotNull(result);
        assertSame(response,result);

        assertEquals("PAT-100001",patient.getPatientId());
        assertEquals(PatientStatus.ACTIVE,patient.getStatus());

        verify(patientValidator).validateForCreate(request);
        verify(patientRepository).existsByEmail("patient@test.com");
        verify(patientIdGenerator).generate();
        verify(patientRepository).save(patient);
        verify(patientMapper).toResponse(patient);
    }

    @Test
    void registerPatient_shouldThrowDuplicateExceptionWhenEmailAlreadyExists() {
        PatientRequest request = mock(PatientRequest.class);

        when(request.getEmail()).thenReturn("existing@test.com");
        when(patientRepository.existsByEmail("existing@test.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class,()->patientService.registerPatient(request));
        verify(patientValidator).validateForCreate(request);
        verify(patientRepository).existsByEmail("existing@test.com");
        verify(patientRepository,never()).save(any(Patient.class));
        verify(patientMapper,never()).toEntity(any());
    }

    @Test
    void registerPatient_shouldThrowDuplicateExceptionWhenDocumentNumbersDuplicateInRequest() {
        PatientRequest request = mock(PatientRequest.class);

        IdentityDocumentRequest firstDocument = mock(IdentityDocumentRequest.class);
        IdentityDocumentRequest secondDocument = mock(IdentityDocumentRequest.class);

        when(request.getEmail()).thenReturn("patient@test.com");
        when(firstDocument.getDocumentNumber()).thenReturn("DOC-123");
        when(secondDocument.getDocumentNumber()).thenReturn("DOC-123");
        when(request.getIdentityDocuments()).thenReturn(List.of(firstDocument,secondDocument));
        when(patientRepository.existsByEmail("patient@test.com")).thenReturn(false);

        assertThrows(DuplicateResourceException.class,()->patientService.registerPatient(request));

        verify(identityDocumentRepository, never()).findExistingDocumentNumbers(anySet());
        verify(patientRepository, never()).save(any(Patient.class));
    }
    @Test
    void registerPatient_shouldThrowDuplicateExceptionWhenDocumentAlreadyExists() {
        PatientRequest request = mock(PatientRequest.class);
        IdentityDocumentRequest document = mock(IdentityDocumentRequest.class);

        when(request.getEmail()).thenReturn("patient@test.com");
        when(document.getDocumentNumber()).thenReturn("DOC-123");
        when(request.getIdentityDocuments()).thenReturn(List.of(document));
        when(patientRepository.existsByEmail("patient@test.com")).thenReturn(false);
        when(identityDocumentRepository.findExistingDocumentNumbers(anySet())).thenReturn(List.of("DOC-123"));

        assertThrows(DuplicateResourceException.class,()->patientService.registerPatient(request));

        verify(identityDocumentRepository).findExistingDocumentNumbers(anySet());
        verify(patientRepository,never()).save(any(Patient.class));
    }

    @Test
    void registerPatient_shouldSetChildRelationshipsAndDefaultStates(){
        PatientRequest request = mock(PatientRequest.class);

        Address address = new Address();
        EmergencyContact contact = new EmergencyContact();
        IdentityDocument document = new IdentityDocument();

        PatientResponse response = mock(PatientResponse.class);

        when(request.getEmail()).thenReturn("patient@test.com");
        when(request.getIdentityDocuments()).thenReturn(List.of());
        when(patientRepository.existsByEmail("patient@test.com")).thenReturn(false);
        when(identityDocumentRepository.findExistingDocumentNumbers(anySet())).thenReturn(List.of());

        patient.getAddresses().add(address);
        patient.getEmergencyContacts().add(contact);
        patient.getIdentityDocuments().add(document);

        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientIdGenerator.generate()).thenReturn("PAT-100001");
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        patientService.registerPatient(request);

        assertSame(patient, address.getPatient());
        assertTrue(address.isActive());

        assertSame(patient, contact.getPatient());
        assertTrue(contact.isActive());

        assertSame(patient, document.getPatient());
        assertTrue(document.isActive());
        assertFalse(document.isVerified());
    }

    /*GET PATIENT*/
    @Test
    void getPatientByPatientId_shouldReturnPatientSuccessfully() {

        String patientId = "PAT-100001";

        PatientResponse response = mock(PatientResponse.class);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(patientMapper.toResponse(patient))
                .thenReturn(response);

        PatientResponse result =
                patientService.getPatientByPatientId(patientId);

        assertNotNull(result);
        assertSame(response, result);

        verify(patientRepository)
                .findByPatientId(patientId);

        verify(patientMapper)
                .toResponse(patient);
    }

    @Test
    void getPatientByPatientId_shouldThrowResourceNotFoundException() {

        String patientId = "PAT-999999";

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.getPatientByPatientId(patientId)
        );

        verify(patientRepository)
                .findByPatientId(patientId);

        verify(patientMapper, never())
                .toResponse(any());
    }

    /*GET ALL PATIENTS*/
    @Test
    void getAllPatients_shouldReturnPagedPatients() {

        Patient secondPatient = new Patient();

        PatientResponse firstResponse =
                mock(PatientResponse.class);

        PatientResponse secondResponse =
                mock(PatientResponse.class);

        Page<Patient> patientPage =
                new PageImpl<>(List.of(patient, secondPatient));

        when(patientRepository.findAll(any(Pageable.class)))
                .thenReturn(patientPage);

        when(patientMapper.toResponse(patient))
                .thenReturn(firstResponse);

        when(patientMapper.toResponse(secondPatient))
                .thenReturn(secondResponse);

        Page<PatientResponse> result =
                patientService.getAllPatients(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        assertEquals(
                firstResponse,
                result.getContent().get(0)
        );

        assertEquals(
                secondResponse,
                result.getContent().get(1)
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(patientRepository)
                .findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    /*UPDATE PATIENT*/
    @Test
    void updatePatient_shouldUpdatePatientSuccessfully() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        PatientResponse response =
                mock(PatientResponse.class);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(patientMapper.toResponse(patient))
                .thenReturn(response);

        PatientResponse result =
                patientService.updatePatient(
                        patientId,
                        request
                );

        assertNotNull(result);
        assertSame(response, result);

        verify(patientValidator)
                .validateForUpdate(request);

        verify(patientRepository)
                .findByPatientId(patientId);

        verify(patientMapper)
                .updateEntity(request, patient);

        verify(patientMapper)
                .toResponse(patient);
    }

    @Test
    void updatePatient_shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist() {

        String patientId = "PAT-999999";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(patientValidator)
                .validateForUpdate(request);

        verify(patientMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void updatePatient_shouldThrowDuplicateExceptionWhenEmailBelongsToAnotherPatient() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        when(request.getEmail())
                .thenReturn("another@test.com");

        patient.setEmail("patient@test.com");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(patientRepository.existsByEmailAndPatientIdNot(
                "another@test.com",
                patientId
        )).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(patientRepository)
                .existsByEmailAndPatientIdNot(
                        "another@test.com",
                        patientId
                );

        verify(patientMapper, never())
                .updateEntity(any(), any());
    }

    /*UPDATE - IDENTITY DOCUMENT DUPLICATES*/
    @Test
    void updatePatient_shouldThrowDuplicateExceptionWhenNewDocumentAlreadyExists() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocumentUpdateRequest documentRequest =
                mock(IdentityDocumentUpdateRequest.class);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getIdentityDocuments())
                .thenReturn(List.of(documentRequest));

        when(documentRequest.getId())
                .thenReturn(null);

        when(documentRequest.getDocumentNumber())
                .thenReturn("DOC-123");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(identityDocumentRepository.existsByDocumentNumber(
                "DOC-123"
        )).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(identityDocumentRepository)
                .existsByDocumentNumber("DOC-123");

        verify(patientMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void updatePatient_shouldThrowDuplicateExceptionWhenDocumentNumbersDuplicateInRequest() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocumentUpdateRequest first =
                mock(IdentityDocumentUpdateRequest.class);

        IdentityDocumentUpdateRequest second =
                mock(IdentityDocumentUpdateRequest.class);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getIdentityDocuments())
                .thenReturn(List.of(first, second));

        when(first.getDocumentNumber())
                .thenReturn("DOC-123");

        when(second.getDocumentNumber())
                .thenReturn("DOC-123");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        assertThrows(
                DuplicateResourceException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(identityDocumentRepository, never())
                .existsByDocumentNumber(anyString());

        verify(patientMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void updatePatient_shouldValidateExistingDocumentWhenDocumentIdProvided() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocumentUpdateRequest documentRequest =
                mock(IdentityDocumentUpdateRequest.class);

        IdentityDocument existingDocument =
                new IdentityDocument();

        existingDocument.setId(25L);
        existingDocument.setDocumentNumber("DOC-123");
        existingDocument.setActive(true);

        patient.getIdentityDocuments().add(existingDocument);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getIdentityDocuments())
                .thenReturn(List.of(documentRequest));

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(documentRequest.getId())
                .thenReturn(25L);

        when(documentRequest.getDocumentNumber())
                .thenReturn("DOC-123");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(identityDocumentRepository
                .existsByDocumentNumberAndPatientIdNot(
                        "DOC-123",
                        25L
                ))
                .thenReturn(false);

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(patientId, request);

        verify(identityDocumentRepository)
                .existsByDocumentNumberAndPatientIdNot(
                        "DOC-123",
                        25L
                );
    }

    /*UPDATE - ADDRESS SYNCHRONIZATION*/
    @Test
    void updatePatient_shouldDeactivateAddressRemovedFromRequest() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        Address existingAddress = new Address();

        existingAddress.setId(10L);
        existingAddress.setActive(true);
        existingAddress.setPrimaryAddress(true);

        patient.getAddresses()
                .add(existingAddress);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(
                patientId,
                request
        );

        assertFalse(existingAddress.isActive());
        assertFalse(existingAddress.isPrimaryAddress());
    }

    @Test
    void updatePatient_shouldThrowBadRequestWhenAddressIdDoesNotBelongToPatient() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        AddressUpdateRequest addressRequest =
                mock(AddressUpdateRequest.class);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of(addressRequest));

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(addressRequest.getId())
                .thenReturn(999L);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        assertThrows(
                BadRequestException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(addressMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void updatePatient_shouldCreateNewAddressWhenRequestAddressHasNoId() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        AddressUpdateRequest addressRequest =
                mock(AddressUpdateRequest.class);

        Address newAddress = new Address();

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of(addressRequest));

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(addressRequest.getId())
                .thenReturn(null);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(addressMapper.toEntity(addressRequest))
                .thenReturn(newAddress);

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(
                patientId,
                request
        );

        assertTrue(patient.getAddresses()
                .contains(newAddress));

        assertSame(
                patient,
                newAddress.getPatient()
        );

        assertTrue(newAddress.isActive());

        verify(addressMapper)
                .toEntity(addressRequest);
    }

    /*UPDATE - EMERGENCY CONTACT SYNCHRONIZATION*/
    @Test
    void updatePatient_shouldDeactivateEmergencyContactRemovedFromRequest() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        EmergencyContact existingContact =
                new EmergencyContact();

        existingContact.setId(20L);
        existingContact.setActive(true);
        existingContact.setPrimaryContact(true);

        patient.getEmergencyContacts()
                .add(existingContact);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(
                patientId,
                request
        );

        assertFalse(existingContact.isActive());
        assertFalse(existingContact.isPrimaryContact());
    }

    @Test
    void updatePatient_shouldThrowBadRequestWhenEmergencyContactIdDoesNotBelongToPatient() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        EmergencyContactUpdateRequest contactRequest =
                mock(EmergencyContactUpdateRequest.class);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of(contactRequest));

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(contactRequest.getId())
                .thenReturn(999L);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        assertThrows(
                BadRequestException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(emergencyContactMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void updatePatient_shouldCreateNewEmergencyContactWhenRequestHasNoId() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        EmergencyContactUpdateRequest contactRequest =
                mock(EmergencyContactUpdateRequest.class);

        EmergencyContact newContact =
                new EmergencyContact();

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of(contactRequest));

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(contactRequest.getId())
                .thenReturn(null);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(emergencyContactMapper.toEntity(contactRequest))
                .thenReturn(newContact);

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(
                patientId,
                request
        );

        assertTrue(patient.getEmergencyContacts()
                .contains(newContact));

        assertSame(
                patient,
                newContact.getPatient()
        );

        assertTrue(newContact.isActive());

        verify(emergencyContactMapper)
                .toEntity(contactRequest);
    }

    /*UPDATE - IDENTITY DOCUMENT SYNCHRONIZATION*/
    @Test
    void updatePatient_shouldDeactivateIdentityDocumentRemovedFromRequest() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocument existingDocument =
                new IdentityDocument();

        existingDocument.setId(30L);
        existingDocument.setActive(true);

        patient.getIdentityDocuments()
                .add(existingDocument);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of());

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(
                patientId,
                request
        );

        assertFalse(existingDocument.isActive());
    }

    @Test
    void updatePatient_shouldThrowBadRequestWhenIdentityDocumentIdDoesNotBelongToPatient() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocumentUpdateRequest documentRequest =
                mock(IdentityDocumentUpdateRequest.class);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of(documentRequest));

        when(documentRequest.getId())
                .thenReturn(999L);

        when(documentRequest.getDocumentNumber())
                .thenReturn("DOC-999");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(identityDocumentRepository
                .existsByDocumentNumberAndPatientIdNot(
                        "DOC-999",
                        999L
                )).thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> patientService.updatePatient(
                        patientId,
                        request
                )
        );

        verify(identityDocumentMapper, never())
                .updateEntity(any(), any());
    }

    @Test
    void updatePatient_shouldResetVerificationWhenDocumentNumberChanges() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocumentUpdateRequest documentRequest =
                mock(IdentityDocumentUpdateRequest.class);

        IdentityDocument existingDocument =
                new IdentityDocument();

        existingDocument.setId(30L);
        existingDocument.setDocumentNumber("DOC-OLD");
        existingDocument.setVerified(true);
        existingDocument.setActive(true);

        patient.getIdentityDocuments()
                .add(existingDocument);

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of(documentRequest));

        when(documentRequest.getId())
                .thenReturn(30L);

        when(documentRequest.getDocumentNumber())
                .thenReturn("DOC-NEW");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(identityDocumentRepository
                .existsByDocumentNumberAndPatientIdNot(
                        "DOC-NEW",
                        30L
                )).thenReturn(false);

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        doAnswer(invocation -> {
            IdentityDocumentUpdateRequest source =
                    invocation.getArgument(0);

            IdentityDocument target =
                    invocation.getArgument(1);

            target.setDocumentNumber(
                    source.getDocumentNumber()
            );

            return null;
        }).when(identityDocumentMapper)
                .updateEntity(
                        documentRequest,
                        existingDocument
                );

        patientService.updatePatient(
                patientId,
                request
        );

        assertEquals(
                "DOC-NEW",
                existingDocument.getDocumentNumber()
        );

        assertFalse(existingDocument.isVerified());

        assertTrue(existingDocument.isActive());

        assertSame(
                patient,
                existingDocument.getPatient()
        );
    }

    @Test
    void updatePatient_shouldCreateNewIdentityDocumentWhenRequestHasNoId() {

        String patientId = "PAT-100001";

        PatientUpdateRequest request =
                mock(PatientUpdateRequest.class);

        IdentityDocumentUpdateRequest documentRequest =
                mock(IdentityDocumentUpdateRequest.class);

        IdentityDocument newDocument =
                new IdentityDocument();

        when(request.getEmail())
                .thenReturn("patient@test.com");

        when(request.getAddresses())
                .thenReturn(List.of());

        when(request.getEmergencyContacts())
                .thenReturn(List.of());

        when(request.getIdentityDocuments())
                .thenReturn(List.of(documentRequest));

        when(documentRequest.getId())
                .thenReturn(null);

        when(documentRequest.getDocumentNumber())
                .thenReturn("DOC-NEW");

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        when(identityDocumentRepository
                .existsByDocumentNumber("DOC-NEW"))
                .thenReturn(false);

        when(identityDocumentMapper.toEntity(documentRequest))
                .thenReturn(newDocument);

        when(patientMapper.toResponse(patient))
                .thenReturn(mock(PatientResponse.class));

        patientService.updatePatient(
                patientId,
                request
        );

        assertTrue(patient.getIdentityDocuments()
                .contains(newDocument));

        assertSame(
                patient,
                newDocument.getPatient()
        );

        assertTrue(newDocument.isActive());

        assertFalse(newDocument.isVerified());

        verify(identityDocumentMapper)
                .toEntity(documentRequest);
    }

    /*DELETE / SOFT DELETE*/

    @Test
    void deletePatient_shouldSoftDeletePatientAndDeactivateChildren() {

        String patientId = "PAT-100001";

        Address address = new Address();
        address.setActive(true);

        EmergencyContact contact =
                new EmergencyContact();

        contact.setActive(true);

        IdentityDocument document =
                new IdentityDocument();

        document.setActive(true);

        patient.getAddresses().add(address);
        patient.getEmergencyContacts().add(contact);
        patient.getIdentityDocuments().add(document);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        patientService.deletePatient(patientId);

        assertEquals(
                PatientStatus.INACTIVE,
                patient.getStatus()
        );

        assertFalse(address.isActive());
        assertFalse(contact.isActive());
        assertFalse(document.isActive());

        verify(patientRepository)
                .findByPatientId(patientId);

        verify(patientRepository, never())
                .delete(any());
    }

    @Test
    void deletePatient_shouldDoNothingWhenPatientAlreadyInactive() {

        String patientId = "PAT-100001";

        patient.setStatus(PatientStatus.INACTIVE);

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.of(patient));

        patientService.deletePatient(patientId);

        assertEquals(
                PatientStatus.INACTIVE,
                patient.getStatus()
        );

        verify(patientRepository)
                .findByPatientId(patientId);

        verify(patientRepository, never())
                .delete(any());
    }

    @Test
    void deletePatient_shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist() {

        String patientId = "PAT-999999";

        when(patientRepository.findByPatientId(patientId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.deletePatient(patientId)
        );

        verify(patientRepository)
                .findByPatientId(patientId);
    }
}

