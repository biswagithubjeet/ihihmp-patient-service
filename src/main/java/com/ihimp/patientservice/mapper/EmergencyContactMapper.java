package com.ihimp.patientservice.mapper;

import com.ihimp.patientservice.dto.request.EmergencyContactRequest;
import com.ihimp.patientservice.dto.request.EmergencyContactUpdateRequest;
import com.ihimp.patientservice.dto.response.EmergencyContactResponse;
import com.ihimp.patientservice.entity.EmergencyContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EmergencyContactMapper {

    //Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    EmergencyContact toEntity(EmergencyContactRequest request);

    //Response
    EmergencyContactResponse toResponse(EmergencyContact entity);

    // Update existing entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateEntity(EmergencyContactUpdateRequest request,
                      @MappingTarget EmergencyContact entity);

    // Create new emergency contact during patient update
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    EmergencyContact toEntity(EmergencyContactUpdateRequest request);
}
