package com.ihimp.patientservice.dto.request;

import com.ihimp.patientservice.enums.RelationshipType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotNull(message = "Relationship type is required")
    private RelationshipType relationshipType;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+[1-9]\\d{0,3}$", message = "Invalid country code")
    private String countryCode;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid mobile number")
    private String mobileNumber;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid alternate mobile number")
    private String alternateMobileNumber;

    @Email(message = "Invalid email address")
    @Size(max = 254, message = "Email cannot exceed 150 characters")
    private String email;

    @NotNull(message = "Primary contact flag is required")
    private Boolean primaryContact;
}
