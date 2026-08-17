package com.ihimp.patientservice.constant;

public final class PatientConstants {

    private PatientConstants() {

    }

    public static final String EMAIL_DUPLICATE_MESSAGE =
            "Patient with this email address already exists: ";

    public static final String IDENTITY_DOCUMENT_DUPLICATE_MESSAGE =
            "Patient with this identity document already exists: ";

    public static final String DATE_OF_BIRTH_FUTURE_MESSAGE =
            "Date of birth cannot be in the future";

    public static final String IDENTITY_DOCUMENT_EXPIRY_MESSAGE =
            "Identity document expiry date cannot be before issue date";

    public static final String VALIDATION_FAILED_MESSAGE =
            "Validation failed";

    public static final String INTERNAL_SERVER_ERROR_MESSAGE =
            "An unexpected error occurred";

    public static final String PATIENT_NOT_FOUND_MESSAGE =
            "Patient not found with this patientId: ";

    public static final String CREATED_AT_FIELD = "createdAt";

    public static final String INVALID_ADDRESS_ID_MESSAGE =
            "Address does not belong to patient: ";

    public static final String INVALID_EMERGENCY_CONTACT_ID_MESSAGE =
            "Emergency contact does not belong to patient: ";

    public static final String INVALID_IDENTITY_DOCUMENT_ID_MESSAGE =
            "Identity document does not belong to patient: ";

    public static final String DUPLICATE_PATIENT_RESOURCE_MESSAGE =
            "Patient already exists with the provided unique information";

    public static final String DEFAULT_PAGE_NUMBER = "0";

    public static final String DEFAULT_PAGE_SIZE = "20";

    public static final long MIN_PAGE_NUMBER = 0L;

    public static final long MIN_PAGE_SIZE = 1L;

    public static final long MAX_PAGE_SIZE = 100L;

}
