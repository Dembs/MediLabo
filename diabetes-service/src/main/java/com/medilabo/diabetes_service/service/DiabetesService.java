package com.medilabo.diabetes_service.service;

import com.medilabo.diabetes_service.config.DiabetesConstants;
import com.medilabo.diabetes_service.dto.NoteDTO;
import com.medilabo.diabetes_service.dto.PatientDTO;
import com.medilabo.diabetes_service.enums.DiabetesRiskLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.medilabo.diabetes_service.proxies.NoteProxy;
import com.medilabo.diabetes_service.proxies.PatientProxy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe d'évaluation du risque de diabète.
 * Elle implémente la logique métier pour évaluer le niveau de risque de diabète
 * d'un patient en fonction de son âge, son genre et des termes médicaux présents
 * dans ses notes médicales.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiabetesService {

    private final PatientProxy patientProxy;
    private final NoteProxy noteProxy;


    /**
     * Evalue le risque de diabète d'un patient en récupérant les données du patient,
     * ses notes médicales, en comptant les déclencheurs et en appliquant les règles métier.
     *
     * @param patientId Identifiant du patient à évaluer
     * @return Le niveau de risque de diabète calculé (NONE, BORDERLINE, IN_DANGER ou EARLY_ONSET)
     */
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

    /**
     * Récupère les informations d'un patient depuis le service patient.
     *
     * @param patientId Identifiant du patient à récupérer
     * @return Les données du patient ou null si le patient n'existe pas ou si une erreur survient
     */
    private PatientDTO getPatientInfo(int patientId) {


        try {
            return patientProxy.getPatientById(patientId);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Patient non trouvé (via Feign) avec ID {}: {}", patientId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations patient (via Feign) pour ID {}: {}", patientId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Récupère les notes médicales d'un patient depuis le service de notes.
     *
     * @param patientId Identifiant du patient dont on veut récupérer les notes
     * @return Liste des notes médicales du patient ou une liste vide si aucune note n'existe
     */
    private List<NoteDTO> getPatientNotes(int patientId) {

        try {
            return noteProxy.getNotesByPatientId(patientId);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Aucune note trouvée (via Feign) pour le patient ID {}", patientId);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notes patient (via Feign) pour ID {}: {}", patientId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Calcule l'âge d'une personne à partir de sa date de naissance.
     *
     * @param dateOfBirth Date de naissance de la personne
     * @return L'âge en années ou 0 si la date de naissance est null
     */
    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            log.warn("Date de naissance nulle, impossible de calculer l'âge.");
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre de termes déclencheurs uniques présents dans les notes d'un patient.
     * Les termes déclencheurs sont définis dans DiabetesConstants.TRIGGER_TERMS.
     *
     * @param notes Liste des notes médicales du patient
     * @return Le nombre de termes déclencheurs uniques trouvés
     */
    private long countUniqueTriggers(List<NoteDTO> notes) {
        if (notes == null || notes.isEmpty()) {
            return 0;
        }

        Set<String> foundTriggers = new HashSet<>();

        // Convertir toutes les notes en une seule chaîne en minuscules pour une recherche globale
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

    /**
     * Détermine le niveau de risque de diabète en fonction de l'âge, du genre et du nombre de déclencheurs.
     *
     * @param age Âge du patient en années
     * @param gender Genre du patient ('M' pour masculin, 'F' pour féminin)
     * @param triggerCount Nombre de termes déclencheurs uniques trouvés dans les notes
     * @return Le niveau de risque de diabète déterminé selon les règles métier
     */
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