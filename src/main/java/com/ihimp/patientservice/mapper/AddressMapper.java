package com.ihimp.patientservice.mapper;

import com.ihimp.patientservice.dto.request.AddressRequest;
import com.ihimp.patientservice.dto.request.AddressUpdateRequest;
import com.ihimp.patientservice.dto.response.AddressResponse;
import com.ihimp.patientservice.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AddressMapper {

    // Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Address toEntity(AddressRequest request);

    // Response
    AddressResponse toResponse(Address entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateEntity(AddressUpdateRequest request, @MappingTarget Address entity);

    // Create new child during patient update
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Address toEntity(AddressUpdateRequest request);
}
