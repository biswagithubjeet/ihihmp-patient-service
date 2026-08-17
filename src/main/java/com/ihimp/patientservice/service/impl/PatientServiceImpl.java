package com.ihimp.patientservice.service.impl;

import com.ihimp.patientservice.constant.PatientConstants;
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
import com.ihimp.patientservice.service.PatientService;
import com.ihimp.patientservice.validator.PatientValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    /*Save and retrieve patients*/
    private final PatientRepository patientRepository;
    /*Address operations (future use)*/
    private final AddressRepository addressRepository;
    /*Emergency contact operations*/
    private final EmergencyContactRepository emergencyContactRepository;
    /*Identity document operations*/
    private final IdentityDocumentRepository identityDocumentRepository;
    /*Patient DTO ↔ Entity*/
    private final PatientMapper patientMapper;
    /*Address DTO ↔ Entity*/
    private final AddressMapper addressMapper;
    /*Emergency Contact DTO ↔ Entity*/
    private final EmergencyContactMapper emergencyContactMapper;
    /*Identity Document DTO ↔ Entity*/
    private final IdentityDocumentMapper identityDocumentMapper;

    private final PatientIdGenerator patientIdGenerator;

    private final PatientValidator  patientValidator;

    @Override
    @Transactional
    public PatientResponse registerPatient(PatientRequest request) {

        patientValidator.validateForCreate(request);

        if(patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    PatientConstants.EMAIL_DUPLICATE_MESSAGE + request.getEmail()
            );
        }

        validateIdentityDocumentDuplicatesForCreate(request);

        Patient patient = patientMapper.toEntity(request);

        patient.setPatientId(patientIdGenerator.generate());

        patient.setStatus(PatientStatus.ACTIVE);

        patient.getAddresses().forEach(address -> {
            address.setPatient(patient);
            address.setActive(true);
        });

        patient.getEmergencyContacts().forEach(contact -> {
            contact.setPatient(patient);
            contact.setActive(true);
        });

        patient.getIdentityDocuments().forEach(document -> {
            document.setPatient(patient);
            document.setActive(true);
            document.setVerified(false);
        });

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toResponse(savedPatient);
    }

    private void validateIdentityDocumentDuplicatesForCreate(
            PatientRequest request) {

        Set<String> documentNumbers = request.getIdentityDocuments()
                .stream()
                .map(IdentityDocumentRequest::getDocumentNumber)
                .collect(Collectors.toSet());

        if (documentNumbers.size()
                != request.getIdentityDocuments().size()) {

            throw new DuplicateResourceException(
                    PatientConstants.IDENTITY_DOCUMENT_DUPLICATE_MESSAGE
            );
        }

        List<String> existingDocumentNumbers =
                identityDocumentRepository.findExistingDocumentNumbers(
                        documentNumbers
                );

        if (!existingDocumentNumbers.isEmpty()) {

            throw new DuplicateResourceException(
                    PatientConstants.IDENTITY_DOCUMENT_DUPLICATE_MESSAGE
                            + existingDocumentNumbers.get(0)
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByPatientId(String patientId) {

        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(PatientConstants.PATIENT_NOT_FOUND_MESSAGE + patientId));
        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, PatientConstants.CREATED_AT_FIELD));

        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(String patientId, PatientUpdateRequest request) {

        patientValidator.validateForUpdate(request);

        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PatientConstants.PATIENT_NOT_FOUND_MESSAGE + patientId));

        if (!patient.getEmail().equalsIgnoreCase(request.getEmail())
                && patientRepository.existsByEmailAndPatientIdNot(request.getEmail(),patientId)) {

            throw new DuplicateResourceException(
                    PatientConstants.EMAIL_DUPLICATE_MESSAGE + request.getEmail());
        }

        validateIdentityDocumentDuplicates(request);

        patientMapper.updateEntity(request, patient);

        synchronizeAddresses(patient, request);

        synchronizeEmergencyContacts(patient, request);

        synchronizeIdentityDocuments(patient, request);

        return patientMapper.toResponse(patient);

    }

    private void validateIdentityDocumentDuplicates(PatientUpdateRequest request){
        Set<String> documentNumbers = new HashSet<>();

        for(IdentityDocumentUpdateRequest documentRequest : request.getIdentityDocuments()){

            String documentNumber = documentRequest.getDocumentNumber();

            if(!documentNumbers.add(documentNumber)){
                throw new DuplicateResourceException(PatientConstants.IDENTITY_DOCUMENT_DUPLICATE_MESSAGE + documentNumber);
            }
            boolean exists;

            if (documentRequest.getId() == null){
                exists = identityDocumentRepository.existsByDocumentNumber(documentNumber);
            }else{
                exists = identityDocumentRepository
                        .existsByDocumentNumberAndPatientIdNot(documentNumber,documentRequest.getId());
            }

            if(exists){
                throw new DuplicateResourceException(
                        PatientConstants.IDENTITY_DOCUMENT_DUPLICATE_MESSAGE + documentNumber);
            }
        }
    }

    private void synchronizeAddresses(Patient patient, PatientUpdateRequest request) {

        Set<Long> requestAddressIds = request.getAddresses().stream().map(AddressUpdateRequest::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        //Deactivate existing addresses removed from the request
        patient.getAddresses().forEach(address -> {
           if (address.getId() != null && !requestAddressIds.contains(address.getId())) {

               address.setActive(false);
               address.setPrimaryAddress(false);
           }
        });

        //reate new or update existing addresses
        request.getAddresses().forEach(addressRequest -> {
            if (addressRequest.getId() == null) {
                Address address = addressMapper.toEntity(addressRequest);

                address.setPatient(patient);
                address.setActive(true);

                patient.getAddresses().add(address);

            }else {
                Address address = patient.getAddresses().stream()
                        .filter(existing -> existing.getId().equals(addressRequest.getId()))
                        .findFirst().orElseThrow(() -> new BadRequestException(
                                PatientConstants.INVALID_ADDRESS_ID_MESSAGE + addressRequest.getId()));

                addressMapper.updateEntity(addressRequest, address);

                address.setPatient(patient);
                address.setActive(true);
            }
        });
    }

    private void synchronizeEmergencyContacts(Patient patient, PatientUpdateRequest request) {
        Set<Long> requestContactIds = request.getEmergencyContacts()
                .stream()
                .map(EmergencyContactUpdateRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        //Deactivate existing contacts removed from the request
        patient.getEmergencyContacts().forEach(contact -> {
           if (contact.getId() != null && !requestContactIds.contains(contact.getId())) {
               contact.setActive(false);
               contact.setPrimaryContact(false);
           }
        });

        // Create new or update existing contacts
        request.getEmergencyContacts().forEach(contactRequest -> {

            if (contactRequest.getId() == null) {
                EmergencyContact contact = emergencyContactMapper.toEntity(contactRequest);

                contact.setPatient(patient);
                contact.setActive(true);
                patient.getEmergencyContacts().add(contact);

            }else{
                EmergencyContact contact = patient.getEmergencyContacts()
                        .stream()
                        .filter(existing -> existing.getId().equals(contactRequest.getId()))
                        .findFirst()
                        .orElseThrow(() -> new BadRequestException(PatientConstants.INVALID_EMERGENCY_CONTACT_ID_MESSAGE + contactRequest.getId()));

                emergencyContactMapper.updateEntity(contactRequest, contact);

                contact.setPatient(patient);
                contact.setActive(true);
            }
        });
    }

    private void synchronizeIdentityDocuments(Patient patient, PatientUpdateRequest request) {

        Set<Long> requestDocumentIds = request.getIdentityDocuments().stream()
                .map(IdentityDocumentUpdateRequest::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        // Deactivate documents removed from the request
        patient.getIdentityDocuments().forEach(document -> {
           if (document.getId() != null && !requestDocumentIds.contains(document.getId())) {
               document.setActive(false);
           }
        });

        // Create new or update existing documents
        request.getIdentityDocuments().forEach(documentRequest -> {
            if (documentRequest.getId() == null) {
                IdentityDocument document = identityDocumentMapper.toEntity(documentRequest);

                document.setPatient(patient);
                document.setActive(true);
                document.setVerified(false);

                patient.getIdentityDocuments().add(document);

            }else {

                IdentityDocument document = patient.getIdentityDocuments()
                        .stream()
                        .filter(existing -> existing.getId().equals(documentRequest.getId()))
                        .findFirst().orElseThrow(() -> new BadRequestException(
                                PatientConstants.INVALID_IDENTITY_DOCUMENT_ID_MESSAGE + documentRequest.getId()));

                String existingDocumentNumber = document.getDocumentNumber();

                identityDocumentMapper.updateEntity(documentRequest, document);

                /*
                 * If the document number changed, previous verification
                 * is no longer valid.
                 */

                if (!existingDocumentNumber.equals(documentRequest.getDocumentNumber())) {

                    document.setVerified(false);
                }

                document.setPatient(patient);
                document.setActive(true);
            }
        });
    }

    @Override
    @Transactional
    public void deletePatient(String patientId) {

        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PatientConstants.PATIENT_NOT_FOUND_MESSAGE + patientId));

        if (patient.getStatus() == PatientStatus.INACTIVE){
            return;
        }

        patient.setStatus(PatientStatus.INACTIVE);

        patient.getAddresses().forEach(address -> address.setActive(false));

        patient.getEmergencyContacts().forEach(contact -> contact.setActive(false));

        patient.getIdentityDocuments().forEach(document -> document.setActive(false));
    }
}
