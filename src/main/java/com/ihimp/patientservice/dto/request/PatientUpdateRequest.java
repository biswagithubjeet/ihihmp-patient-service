package com.ihimp.patientservice.dto.request;

import com.ihimp.patientservice.enums.BloodGroup;
import com.ihimp.patientservice.enums.Gender;
import com.ihimp.patientservice.enums.MaritalStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequest {


    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Marital status is required")
    private MaritalStatus maritalStatus;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "^\\+[1-9]\\d{0,3}$", message = "Invalid country code")
    private String countryCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$",message = "Invalid phone number")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid alternate phone number")
    private String alternatePhoneNumber;

    @NotBlank(message = "Nationality is required")
    @Size(max = 100)
    private String nationality;

    @Size(max = 500, message = "Profile photo URL cannot exceed 500 characters")
    @Pattern(
            regexp = "^(https?://).+",
            message = "Profile photo URL must be a valid HTTP or HTTPS URL"
    )
    private String profilePhotoUrl;

    @Valid
    @NotEmpty(message = "At least one address is required")
    private List<AddressUpdateRequest> addresses;

    @Valid
    @NotEmpty(message = "At least one emergency contact is required")
    private List<EmergencyContactUpdateRequest> emergencyContacts;

    @Valid
    @NotEmpty(message = "At least one identity document is required")
    private List<IdentityDocumentUpdateRequest> identityDocuments;
}
