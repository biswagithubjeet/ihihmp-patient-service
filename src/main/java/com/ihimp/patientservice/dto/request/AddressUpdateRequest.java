package com.ihimp.patientservice.dto.request;

import com.ihimp.patientservice.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {

    private Long id;

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255,message = "Address line 1 cannot exceed 255 characters")
    private String addressLine1;

    @Size(max = 255,message = "Address line 2 cannot exceed 255 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District cannot exceed 100 characters")
    private String district;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @NotBlank(message = "Postal code is required")
    @Pattern(
            regexp = "^[A-Za-z0-9\\-\\s]{3,20}$",
            message = "Invalid postal code"
    )
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    @Size(max = 255, message = "Landmark cannot exceed 255 characters")
    private String landmark;

    @NotNull(message = "Primary address flag is required")
    private Boolean primaryAddress;
}
