package com.medilabo.patient_service.controller;

import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patientsList = patientService.getAllPatients();

        return ResponseEntity.ok(patientsList);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Integer id) {
        Patient patient = patientService.getPatient(id);
        return ResponseEntity.ok(patient);
    }

    @PutMapping("/patient/{id}/update")
    public ResponseEntity<Patient> updatePatient(@PathVariable Integer id, @RequestBody Patient updatedPatient) {
        Patient patient = patientService.updatePatient(updatedPatient,id);
        return ResponseEntity.ok(patient);
    }

    @PostMapping("/patients/create")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient newPatient) {
        Patient patient = patientService.createPatient(newPatient);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }
}
