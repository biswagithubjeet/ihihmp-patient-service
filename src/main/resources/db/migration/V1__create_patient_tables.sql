CREATE TABLE patients (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          patient_id VARCHAR(30) NOT NULL,
                          first_name VARCHAR(100) NOT NULL,
                          middle_name VARCHAR(100),
                          last_name VARCHAR(100) NOT NULL,
                          date_of_birth DATE NOT NULL,
                          gender VARCHAR(20) NOT NULL,
                          blood_group VARCHAR(20),
                          marital_status VARCHAR(20),
                          email VARCHAR(150) NOT NULL,
                          country_code VARCHAR(10) NOT NULL,
                          phone_number VARCHAR(15) NOT NULL,
                          alternate_phone_number VARCHAR(15),
                          nationality VARCHAR(100),
                          profile_photo_url VARCHAR(500),
                          status VARCHAR(20) NOT NULL,
                          created_at DATETIME(6) NOT NULL,
                          updated_at DATETIME(6),

                          CONSTRAINT pk_patients PRIMARY KEY (id),
                          CONSTRAINT uk_patient_id UNIQUE (patient_id),
                          CONSTRAINT uk_email UNIQUE (email)
);

CREATE INDEX idx_patient_id
    ON patients (patient_id);

CREATE INDEX idx_email
    ON patients (email);

CREATE INDEX idx_phone_number
    ON patients (phone_number);


CREATE TABLE addresses (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           address_type VARCHAR(30) NOT NULL,
                           address_line_1 VARCHAR(255) NOT NULL,
                           address_line_2 VARCHAR(255),
                           city VARCHAR(100) NOT NULL,
                           district VARCHAR(100) NOT NULL,
                           state VARCHAR(100) NOT NULL,
                           country VARCHAR(100) NOT NULL,
                           postal_code VARCHAR(20) NOT NULL,
                           landmark VARCHAR(255),
                           is_primary BOOLEAN NOT NULL,
                           active BOOLEAN NOT NULL,
                           patient_id BIGINT NOT NULL,
                           created_at DATETIME(6) NOT NULL,
                           updated_at DATETIME(6),

                           CONSTRAINT pk_addresses PRIMARY KEY (id),
                           CONSTRAINT fk_address_patient
                               FOREIGN KEY (patient_id)
                                   REFERENCES patients (id)
);

CREATE INDEX idx_patient_id
    ON addresses (patient_id);

CREATE INDEX idx_address_type
    ON addresses (address_type);


CREATE TABLE emergency_contacts (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    first_name VARCHAR(100) NOT NULL,
                                    last_name VARCHAR(100),
                                    relationship_type VARCHAR(30) NOT NULL,
                                    country_code VARCHAR(5) NOT NULL,
                                    mobile_number VARCHAR(15) NOT NULL,
                                    alternate_mobile_number VARCHAR(15),
                                    email VARCHAR(254),
                                    is_primary BOOLEAN NOT NULL,
                                    active BOOLEAN NOT NULL,
                                    patient_id BIGINT NOT NULL,
                                    created_at DATETIME(6) NOT NULL,
                                    updated_at DATETIME(6),

                                    CONSTRAINT pk_emergency_contacts PRIMARY KEY (id),
                                    CONSTRAINT fk_emergency_contact_patient
                                        FOREIGN KEY (patient_id)
                                            REFERENCES patients (id)
);

CREATE INDEX idx_patient_id
    ON emergency_contacts (patient_id);

CREATE INDEX idx_relationship_type
    ON emergency_contacts (relationship_type);

CREATE INDEX idx_mobile_number
    ON emergency_contacts (mobile_number);


CREATE TABLE identity_documents (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    document_type VARCHAR(50) NOT NULL,
                                    document_number VARCHAR(100) NOT NULL,
                                    issued_By VARCHAR(150) NOT NULL,
                                    issue_date DATE,
                                    expiry_date DATE,
                                    verified BOOLEAN NOT NULL,
                                    active BOOLEAN NOT NULL,
                                    patient_id BIGINT NOT NULL,
                                    created_at DATETIME(6) NOT NULL,
                                    updated_at DATETIME(6),

                                    CONSTRAINT pk_identity_documents PRIMARY KEY (id),
                                    CONSTRAINT uk_document_number UNIQUE (document_number),
                                    CONSTRAINT fk_identity_document_patient
                                        FOREIGN KEY (patient_id)
                                            REFERENCES patients (id)
);

CREATE INDEX idx_patient_id
    ON identity_documents (patient_id);

CREATE INDEX idx_document_type
    ON identity_documents (document_type);

CREATE INDEX idx_document_number
    ON identity_documents (document_number);


CREATE TABLE patient_sequence (
                                  id BIGINT NOT NULL AUTO_INCREMENT,

                                  CONSTRAINT pk_patient_sequence PRIMARY KEY (id)
);