package com.ihimp.patientservice.mapper;

import com.ihimp.patientservice.dto.request.PatientRequest;
import com.ihimp.patientservice.dto.request.PatientUpdateRequest;
import com.ihimp.patientservice.dto.response.PatientResponse;
import com.ihimp.patientservice.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses={
                AddressMapper.class,
                EmergencyContactMapper.class,
                IdentityDocumentMapper.class
        }
)
public interface PatientMapper {

    /*Create*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "status", ignore = true)
    Patient toEntity(PatientRequest request);

    /*Response*/
    PatientResponse toResponse(Patient entity);

    /*Update existing entity*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "emergencyContacts", ignore = true)
    @Mapping(target = "identityDocuments", ignore = true)
    void updateEntity(
            PatientUpdateRequest request,
            @MappingTarget Patient entity);
}
