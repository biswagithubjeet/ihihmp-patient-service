package com.ihimp.patientservice.repository;

import com.ihimp.patientservice.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact,Long> {

}
