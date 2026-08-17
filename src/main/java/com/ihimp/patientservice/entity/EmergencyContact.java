package com.ihimp.patientservice.entity;

import com.ihimp.patientservice.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name="emergency_contacts",
        indexes={
                @Index(name="idx_patient_id",columnList = "patient_id"),
                @Index(name="idx_relationship_type",columnList = "relationship_type"),
                @Index(name="idx_mobile_number",columnList = "mobile_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name",nullable = false,length = 100)
    private String firstName;

    @Column(name="last_name",length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name="relationship_type",nullable = false,length = 30)
    private RelationshipType relationshipType;

    @Column(name="country_code",nullable = false,length = 5)
    private String countryCode;

    @Column(name="mobile_number",nullable = false,length = 15)
    private String mobileNumber;

    @Column(name="alternate_mobile_number",length = 15)
    private String alternateMobileNumber;

    @Column(length=254)
    private String email;

    @Column(name="is_primary",nullable = false)
    private boolean primaryContact;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name="fk_emergency_contact_patient")
    )
    private Patient patient;
}
