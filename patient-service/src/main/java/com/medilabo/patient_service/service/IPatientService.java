package com.medilabo.patient_service.service;

import com.medilabo.patient_service.model.Patient;

import java.util.List;

/**
 * Interface définissant les opérations de service pour la gestion des patients.
 */
public interface IPatientService {

    /**
     * Récupère la liste complète de tous les patients.
     *
     * @return Liste de tous les patients
     */
    List<Patient> getAllPatients();

    /**
     * Récupère un patient spécifique par son identifiant.
     *
     * @param patientId L'identifiant unique du patient à récupérer
     * @return Le patient correspondant à l'identifiant fourni
     */
    Patient getPatient(int patientId);

    /**
     * Met à jour les informations d'un patient existant.
     *
     * @param patient Objet Patient contenant les nouvelles informations
     * @param patientId L'identifiant du patient à mettre à jour
     * @return Le patient avec les informations mises à jour
     */
    Patient updatePatient(Patient patient, int patientId);

    /**
     * Crée un nouveau patient dans le système.
     *
     * @param patient Objet Patient contenant les informations du nouveau patient
     * @return Le patient créé avec un identifiant généré
     */
    Patient createPatient(Patient patient);

    /**
     * Supprime un patient du système et, par cascade, ses ressources associées
     * dans les autres microservices.
     *
     * @param patientId L'identifiant du patient à supprimer
     * @return true si le patient a été supprimé avec succès, false sinon
     */
    boolean deletePatient(int patientId);
}