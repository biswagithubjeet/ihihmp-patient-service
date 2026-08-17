package com.ihimp.patientservice.mapper;

import com.ihimp.patientservice.dto.request.IdentityDocumentRequest;
import com.ihimp.patientservice.dto.request.IdentityDocumentUpdateRequest;
import com.ihimp.patientservice.dto.response.IdentityDocumentResponse;
import com.ihimp.patientservice.entity.IdentityDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface IdentityDocumentMapper {

    /*Create*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issued_by", source = "issuedBy")
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    IdentityDocument toEntity(IdentityDocumentRequest request);

    /*Response*/
    @Mapping(target = "issuedBy", source = "issued_by")
    IdentityDocumentResponse toResponse(IdentityDocument entity);

    /*Update existing entity*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issued_by", source = "issuedBy")
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateEntity(IdentityDocumentUpdateRequest request,
                      @MappingTarget IdentityDocument entity);

    /*Create new identity document during patient update*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issued_by", source = "issuedBy")
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "patient", ignore = true)
    IdentityDocument toEntity(IdentityDocumentUpdateRequest request);
}
