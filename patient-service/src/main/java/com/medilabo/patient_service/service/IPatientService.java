package com.medilabo.patient_service.service;

import com.medilabo.patient_service.model.Patient;

import java.util.List;

public interface IPatientService {

    List<Patient> getAllPatients();

    Patient getPatient(int patientId);

    Patient updatePatient(Patient patient, int patientId);

    Patient createPatient(Patient patient);
}
