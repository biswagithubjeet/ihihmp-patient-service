package com.ihimp.patientservice.security;

public final class SecurityConstants {

    public static final String PERMISSIONS_CLAIM = "permissions";

    public static final String JWT_SUBJECT_REQUIRED_MESSAGE =
            "JWT subject claim is required";

    public static final String PATIENT_CREATE_PERMISSION = "PATIENT_CREATE";

    public static final String PATIENT_READ_PERMISSION = "PATIENT_READ";

    public static final String PATIENT_UPDATE_PERMISSION = "PATIENT_UPDATE";

    public static final String PATIENT_DELETE_PERMISSION = "PATIENT_DELETE";

    public static final String UNAUTHORIZED_MESSAGE =
            "Authentication is required to access this resource";

    public static final String FORBIDDEN_MESSAGE =
            "You do not have permission to access this resource";

    private SecurityConstants() {
    }
}
