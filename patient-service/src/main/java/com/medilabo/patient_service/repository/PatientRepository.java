package com.medilabo.patient_service.repository;

import com.medilabo.patient_service.model.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<Patient,Integer> {
}
