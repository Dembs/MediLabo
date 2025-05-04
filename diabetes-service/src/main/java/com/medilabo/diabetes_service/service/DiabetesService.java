package com.medilabo.diabetes_service.service;

import com.medilabo.diabetes_service.config.DiabetesConstants;
import com.medilabo.diabetes_service.dto.NoteDTO;
import com.medilabo.diabetes_service.dto.PatientDTO;
import com.medilabo.diabetes_service.enums.DiabetesRiskLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiabetesService {

    private static final Logger log = LoggerFactory.getLogger(DiabetesService.class);

    private final RestTemplate restTemplate;

    @Value("${patient.service.url}")
    private String patientServiceUrl;

    @Value("${note.service.url}")
    private String noteServiceUrl;

    @Autowired
    public DiabetesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    public DiabetesRiskLevel assessDiabetesRisk(int patientId) {

        // Récupérer les informations du patient
        PatientDTO patient = getPatientInfo(patientId);
        if (patient == null) {
            log.warn("Patient non trouvé avec ID: {}. Impossible d'évaluer.", patientId);
            return DiabetesRiskLevel.NONE;
        }
        int age = calculateAge(patient.getBirthdate());
        String gender = patient.getGender();

        // Récupérer les notes du patient
        List<NoteDTO> notes = getPatientNotes(patientId);

        // Compter les déclencheurs uniques
        long uniqueTriggerCount = countUniqueTriggers(notes);
        log.info("Nombre de déclencheurs uniques trouvés pour le patient ID {}: {}", patientId, uniqueTriggerCount);

        // Appliquer les règles de risque
        DiabetesRiskLevel riskLevel = determineRiskLevel(age, gender, uniqueTriggerCount);
        log.info("Niveau de risque calculé pour le patient ID {}: {}", patientId, riskLevel);
        return riskLevel;
    }

    private PatientDTO getPatientInfo(int patientId) {
        String url = patientServiceUrl + "/" + patientId;

        try {
            ResponseEntity<PatientDTO> response = restTemplate.getForEntity(url, PatientDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Patient non trouvé via l'URL {}: {}", url, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations patient depuis {}: {}", url, e.getMessage(), e);

            return null;
        }
    }

    private List<NoteDTO> getPatientNotes(int patientId) {

        String url = noteServiceUrl + "/" + patientId;

        try {
            ResponseEntity<List<NoteDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NoteDTO>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Aucune note trouvée pour le patient ID {} via l'URL {}", patientId, url);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notes patient depuis {}: {}", url, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            log.warn("Date de naissance nulle, impossible de calculer l'âge.");
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private long countUniqueTriggers(List<NoteDTO> notes) {
        if (notes == null || notes.isEmpty()) {
            return 0;
        }

        Set<String> foundTriggers = new HashSet<>();

        // Convertir toutes les notes en une seule chaîne en minuscules pour une recherche globale (simplifie)
        String allNotesContent = notes.stream()
                                      .map(NoteDTO::getNote)
                                      .filter(Objects::nonNull) // Ignorer les notes nulles
                                      .collect(Collectors.joining(" "))
                                      .toLowerCase();

        if(allNotesContent.isBlank()){
            return 0;
        }

        for (String trigger : DiabetesConstants.TRIGGER_TERMS) {
            if (allNotesContent.contains(trigger)) {
                foundTriggers.add(trigger);
            }
        }
        log.debug("Déclencheurs trouvés : {}", foundTriggers);
        return foundTriggers.size();
    }

    private DiabetesRiskLevel determineRiskLevel(int age, String gender, long triggerCount) {
        // Aucun risque si aucun déclencheur
        if (triggerCount == 0) {
            return DiabetesRiskLevel.NONE;
        }

        boolean isMale = "M".equalsIgnoreCase(gender);
        boolean isFemale = "F".equalsIgnoreCase(gender);

        // Early onset
        if ((isMale && age < 30 && triggerCount >= 5) ||
                (isFemale && age < 30 && triggerCount >= 7) ||
                (age >= 30 && triggerCount >= 8)) {
            return DiabetesRiskLevel.EARLY_ONSET;
        }

        // In Danger
        if ((isMale && age < 30 && triggerCount >= 3) ||
                (isFemale && age < 30 && triggerCount >= 4) ||
                (age >= 30 && triggerCount >= 6)) {
            return DiabetesRiskLevel.IN_DANGER;
        }

        // Borderline
        // S'applique SEULEMENT si age > 30 et qu'on n'est pas déjà In Danger ou Early Onset
        if (age > 30 && triggerCount >= 2 && triggerCount <= 5) {
            return DiabetesRiskLevel.BORDERLINE;
        }
        // Si aucune des règles spécifiques ci-dessus ne correspond
        // Ou > 30 ans avec 1 seul déclencheur.
        return DiabetesRiskLevel.NONE;
    }


}