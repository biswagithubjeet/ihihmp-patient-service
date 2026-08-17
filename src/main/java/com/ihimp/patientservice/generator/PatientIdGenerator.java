package com.ihimp.patientservice.generator;

import com.ihimp.patientservice.entity.PatientSequence;
import com.ihimp.patientservice.repository.PatientSequenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class PatientIdGenerator {

    private final PatientSequenceRepository patientSequenceRepository;

    public String generate(){

        PatientSequence sequence = patientSequenceRepository.save(new PatientSequence());

        return String.format("PAT-%d-%06d", Year.now().getValue(), sequence.getId());
    }
}
