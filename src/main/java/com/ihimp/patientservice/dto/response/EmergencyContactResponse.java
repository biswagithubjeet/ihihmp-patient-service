package com.ihimp.patientservice.dto.response;

import com.ihimp.patientservice.enums.RelationshipType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private RelationshipType relationshipType;
    private String countryCode;
    private String mobileNumber;
    private String alternateMobileNumber;
    private String email;
    private Boolean primaryContact;
    private Boolean active;
}
