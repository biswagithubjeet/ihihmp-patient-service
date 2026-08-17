package com.ihimp.patientservice.entity;

import com.ihimp.patientservice.enums.BloodGroup;
import com.ihimp.patientservice.enums.Gender;
import com.ihimp.patientservice.enums.MaritalStatus;
import com.ihimp.patientservice.enums.PatientStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "patients",
        indexes = {
                @Index(name = "idx_patient_id", columnList = "patient_id"),
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_phone_number", columnList = "phone_number")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patient_id", columnNames = "patient_id"),
                @UniqueConstraint(name = "uk_email", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false, length= 30)
    private String patientId;

    @Column(name = "first_name", nullable = false, length= 100)
    private String firstName;

    @Column(name = "middle_name", length= 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length= 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", length = 20)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "country_code",nullable = false, length = 10)
    private String countryCode;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "alternate_phone_number",nullable = true,length = 15)
    private String alternatePhoneNumber;

    @Column(length = 100)
    private String nationality;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PatientStatus status;

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Address> addresses = new ArrayList<>();
    @OneToMany(
            mappedBy = "patient",
            cascade =  CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();
    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<IdentityDocument> identityDocuments = new ArrayList<>();

}
