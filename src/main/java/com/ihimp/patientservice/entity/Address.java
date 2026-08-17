package com.ihimp.patientservice.entity;

import com.ihimp.patientservice.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "addresses",
        indexes = {
                @Index(name= "idx_patient_id", columnList = "patient_id"),
                @Index(name= "idx_address_type", columnList= "address_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type",nullable = false, length = 30)
    private AddressType addressType;

    @Column(name = "address_line_1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name= "address_line_2",length = 255)
    private String addressLine2;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name="postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(length = 255)
    private String landmark;

    @Column(name= "is_primary", nullable = false)
    private boolean primaryAddress;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_address_patient")
    )
    private Patient patient;

}
