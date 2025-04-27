package com.medilabo.patient_service.service;

import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService implements IPatientService {
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAllPatients() {
        return (List<Patient>) patientRepository.findAll();
    }

    @Override
    public Patient getPatient(int patientId) {
        return patientRepository.findById(patientId).orElseThrow(() ->
                new EntityNotFoundException("Patient not found"));
    }

    @Override
    public Patient updatePatient(Patient patient, int patientId) {
        return patientRepository.findById(patientId).map(existingPatient -> { ///Update patient except gender and birthdate
                                    existingPatient.setLastName(patient.getLastName());
                                    existingPatient.setFirstName(patient.getFirstName());

                                    existingPatient.setPhoneNumber(patient.getPhoneNumber());
                                    existingPatient.setAddress(patient.getAddress());
                                    return patientRepository.save(existingPatient);
                                })
                                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    @Override
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public boolean deletePatient(int patientId) {
        if (patientRepository.existsById(patientId)) {
            patientRepository.deleteById(patientId);
            return true;
        }
        return false;
    }
}
