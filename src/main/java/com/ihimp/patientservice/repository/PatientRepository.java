package com.ihimp.patientservice.repository;

import com.ihimp.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientId(String patientId);

    Optional<Patient> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPatientId(String patientId);

    boolean existsByEmailAndPatientIdNot(String email, String patientId);
}
