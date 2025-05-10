package com.medilabo.diabetes_service.controller;

import com.medilabo.diabetes_service.enums.DiabetesRiskLevel;
import com.medilabo.diabetes_service.service.DiabetesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST exposant les API pour l'évaluation du risque de diabète.
 */
@RestController
@RequestMapping("/diabetes")
@RequiredArgsConstructor
public class DiabetesController {

    private static final Logger log = LoggerFactory.getLogger(DiabetesController.class);

    private final DiabetesService diabetesService;

    /**
     * Évalue le risque de diabète pour un patient donné par son ID.
     * Récupère les notes médicales du patient via les services appropriés,
     * analyse leur contenu et détermine le niveau de risque en fonction
     * des termes déclencheurs identifiés.
     *
     * @param patientId L'ID du patient à évaluer
     * @return Le niveau de risque de diabète calculé (NONE, BORDERLINE, IN_DANGER ou EARLY_ONSET)
     */
    @GetMapping("/{patientId}")
    public DiabetesRiskLevel getDiabetesAssessment(@PathVariable int patientId) {
        log.info("Requête reçue pour évaluer le risque du patient ID: {}", patientId);
        DiabetesRiskLevel riskLevel = diabetesService.assessDiabetesRisk(patientId);
        log.info("Évaluation terminée pour patient ID {}. Risque: {}", patientId, riskLevel);
        return riskLevel;
    }
}