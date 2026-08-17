package com.ihimp.patientservice.dto.response;

import com.ihimp.patientservice.enums.AddressType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;
    private AddressType addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String country;
    private String postalCode;
    private String landmark;
    private Boolean primaryAddress;
    private Boolean active;
}
