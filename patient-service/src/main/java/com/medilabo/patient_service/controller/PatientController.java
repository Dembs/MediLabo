package com.medilabo.patient_service.controller;

import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les API de gestion des patients et fournit les endpoints nécessaires pour créer, récupérer, mettre à jour
 * et supprimer des patients.
 */
@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Récupère la liste de tous les patients.
     *
     * @return Liste complète des patients
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patientsList = patientService.getAllPatients();
        return ResponseEntity.ok(patientsList);
    }

    /**
     * Récupère les informations d'un patient spécifique par son identifiant.
     *
     * @param id Identifiant unique du patient à récupérer
     * @return Le patient trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Integer id) {
        Patient patient = patientService.getPatient(id);
        return ResponseEntity.ok(patient);
    }

    /**
     * Met à jour les informations d'un patient existant.
     *
     * @param id Identifiant du patient à mettre à jour
     * @param updatedPatient Objet Patient contenant les nouvelles informations
     * @return Le patient mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Integer id, @RequestBody Patient updatedPatient) {
        Patient patient = patientService.updatePatient(updatedPatient, id);
        return ResponseEntity.ok(patient);
    }

    /**
     * Crée un nouveau patient dans le système.
     *
     * @param newPatient Objet Patient contenant les informations du nouveau patient
     * @return Le patient créé avec son identifiant généré
     */
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient newPatient) {
        Patient patient = patientService.createPatient(newPatient);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    /**
     * Supprime un patient du système et déclenche la suppression en cascade
     * des ressources associées dans les autres microservices.
     *
     * @param id Identifiant du patient à supprimer
     * @return Statut HTTP 204 (NO_CONTENT) si supprimé avec succès, ou 404 (NOT_FOUND) si le patient n'existe pas
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable int id) {
        boolean deleted = patientService.deletePatient(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}