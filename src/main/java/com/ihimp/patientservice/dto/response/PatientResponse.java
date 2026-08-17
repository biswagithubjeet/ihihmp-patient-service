package com.ihimp.patientservice.dto.response;

import com.ihimp.patientservice.enums.BloodGroup;
import com.ihimp.patientservice.enums.Gender;
import com.ihimp.patientservice.enums.MaritalStatus;
import com.ihimp.patientservice.enums.PatientStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {

    private Long id;
    private String patientId;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private BloodGroup bloodGroup;
    private MaritalStatus maritalStatus;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String nationality;
    private String profilePhotoUrl;
    private PatientStatus status;
    private List<AddressResponse> addresses;
    private List<EmergencyContactResponse> emergencyContacts;
    private List<IdentityDocumentResponse> identityDocuments;
}
