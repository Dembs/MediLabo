package com.medilabo.patient_service.service;

import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Implémentation du service de gestion des patients pour le microservice patient-service.
 * Ce service gère les opérations CRUD (Create, Read, Update, Delete) pour les patients,
 * ainsi que la coordination avec d'autres microservices pour maintenir la cohérence des données
 * lors de la suppression d'un patient.
 */
@Service
public class PatientService implements IPatientService {
    private final PatientRepository patientRepository;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    /**
     * URL de la gateway API, injectée depuis la configuration.
     */
    @Value("${api.gateway.url}")
    private String apiGatewayUrl;

    @Autowired
    public PatientService(PatientRepository patientRepository, RestTemplate restTemplate) {
        this.patientRepository = patientRepository;
        this.restTemplate = restTemplate;
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
        return patientRepository.findById(patientId).map(existingPatient -> { // Update patient except gender and birthdate
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
    @Transactional
    public boolean deletePatient(int patientId) {
        if (!patientRepository.existsById(patientId)) {
            return false;
        }

        // Supprimer les notes associées via l'API Gateway
        String deleteNotesUrl = apiGatewayUrl + "/notes/" + patientId; // Construire l'URL

        try {
            // Authentification
            String auth = "user" + ":" + "password";
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Appel DELETE à notes-service via gateway
            ResponseEntity<Void> response = restTemplate.exchange(
                    deleteNotesUrl,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        }
        catch (RestClientException e) {
            log.error("Erreur RestClient [...] Le patient sera quand même supprimé.", patientId, e.getMessage());
        } catch (Exception e) {
            log.error("Erreur inattendue [...] Le patient sera quand même supprimé.", patientId, e.getMessage());
        }

        // Supprimer le patient de la base de données locale (SQL)
        patientRepository.deleteById(patientId);

        return true;
    }
}