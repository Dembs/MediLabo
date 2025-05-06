package com.medilabo.patient_service.repository;

import com.medilabo.patient_service.model.Patient;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository pour l'accès et la manipulation des entités Patient dans la base de données.
 */
public interface PatientRepository extends CrudRepository<Patient, Integer> {
}