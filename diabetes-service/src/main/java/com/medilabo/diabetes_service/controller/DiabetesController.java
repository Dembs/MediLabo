package com.medilabo.diabetes_service.controller;

import com.medilabo.diabetes_service.enums.DiabetesRiskLevel;
import com.medilabo.diabetes_service.service.DiabetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diabetes")
public class DiabetesController {

    private static final Logger log = LoggerFactory.getLogger(DiabetesController.class);

    private final DiabetesService diabetesService;

    @Autowired
    public DiabetesController(DiabetesService diabetesService) {
        this.diabetesService = diabetesService;
    }

    /**
     * Évalue le risque de diabète pour un patient donné par son ID.
     *
     * @param patientId L'ID du patient à évaluer.
     * @return Le niveau de risque de diabète calculé (sérialisé en JSON).
     */
    @GetMapping("/{patientId}")
    public DiabetesRiskLevel getDiabetesAssessment(@PathVariable int patientId) {
        log.info("Requête reçue pour évaluer le risque du patient ID: {}", patientId);
        DiabetesRiskLevel riskLevel = diabetesService.assessDiabetesRisk(patientId);
        log.info("Évaluation terminée pour patient ID {}. Risque: {}", patientId, riskLevel);
        return riskLevel;

        /* Alternative avec ResponseEntity pour plus de contrôle (ex: codes de statut)
           try {
               DiabetesRiskLevel riskLevel = diabetesAssessmentService.assessDiabetesRisk(patientId);
               log.info("Évaluation terminée pour patient ID {}. Risque: {}", patientId, riskLevel);
               return ResponseEntity.ok(riskLevel);
           } catch (PatientNotFoundException e) { // Si vous définissez des exceptions spécifiques
               log.warn("Tentative d'évaluation pour patient non trouvé ID: {}", patientId);
               return ResponseEntity.notFound().build();
           } catch (Exception e) {
               log.error("Erreur interne lors de l'évaluation du patient ID: {}", patientId, e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
           }
        */
    }

    // Vous pourriez ajouter d'autres endpoints ici si nécessaire, par exemple :
    // - Un endpoint POST qui prendrait directement les infos patient et notes en body
    //   (moins microservice, mais possible)
    // - Un endpoint pour évaluer sur la base du nom/prénom (nécessiterait d'abord un appel à patient-service pour trouver l'ID)

}