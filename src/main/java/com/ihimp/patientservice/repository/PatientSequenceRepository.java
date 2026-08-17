package com.ihimp.patientservice.repository;

import com.ihimp.patientservice.entity.PatientSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientSequenceRepository extends JpaRepository<PatientSequence,Long> {
}
