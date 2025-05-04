package com.medilabo.front_end.controller;

import com.medilabo.front_end.model.Note;
import com.medilabo.front_end.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/ui")
public class PatientUiController {

    private static final Logger log = LoggerFactory.getLogger(PatientUiController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.gateway.url:http://localhost:8080}")
    private String apiGatewayUrl;

    @Value("${backend.api.username}")
    private String backendApiUsername;

    @Value("${backend.api.password}")
    private String backendApiPassword;

    @GetMapping("/patients")
    public String listPatients(Model model) {

        String url = apiGatewayUrl + "/patients";

        try {
            // Création de l'en-tête d'authentification Basic
            String auth = backendApiUsername + ":" + backendApiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Appel à l'API via RestTemplate
            ResponseEntity<List<Patient>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Patient>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                model.addAttribute("patients", response.getBody());
            } else {
                log.warn("Réponse non OK reçue de l'API Gateway : {}", response.getStatusCode());
                model.addAttribute("errorMessage", "Impossible de récupérer la liste des patients. Statut : " + response.getStatusCode());
                model.addAttribute("patients", Collections.emptyList());
            }
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des patients UI", e);
            model.addAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            model.addAttribute("patients", Collections.emptyList());
        }

        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String viewPatient(@PathVariable("id") int id, Model model) {
        String patientUrl = apiGatewayUrl + "/patients/" + id;
        String notesUrl = apiGatewayUrl + "/notes/" + id;
        String diabetesUrl = apiGatewayUrl + "/diabetes/" + id;

        String auth = backendApiUsername + ":" + backendApiPassword;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        boolean patientFound = false;

        try {
            ResponseEntity<Patient> patientResponse = restTemplate.exchange(
                    patientUrl,
                    HttpMethod.GET,
                    entity,
                    Patient.class
            );

            if (patientResponse.getStatusCode() == HttpStatus.OK && patientResponse.getBody() != null) {
                model.addAttribute("patient", patientResponse.getBody());
                patientFound = true;
            } else {
                log.warn("Patient ID {} non trouvé ou erreur API (Patient): {}", id, patientResponse.getStatusCode());
                model.addAttribute("errorMessage", "Patient non trouvé (Code: " + patientResponse.getStatusCode() + ")");
                model.addAttribute("patientNotes", new ArrayList<Note>()); // Liste vide pour la vue
                return "patient-details";
            }
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération du patient ID {}", id, e);
            model.addAttribute("errorMessage", "Erreur inattendue (Patient).");
            model.addAttribute("patientNotes", new ArrayList<Note>());
            return "patient-details";
        }

        //Si le patient a été trouvé, récupérer ses notes
        if (patientFound) {
            try {
                ResponseEntity<List<Note>> notesResponse = restTemplate.exchange(
                        notesUrl,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<Note>>() {}
                );

                if (notesResponse.getStatusCode() == HttpStatus.OK) {
                    List<Note> notes = notesResponse.getBody();
                    model.addAttribute("patientNotes", notes != null ? notes : new ArrayList<Note>());
                } else {
                    log.warn("Erreur lors de la récupération des notes pour le patient ID {}, statut: {}", id, notesResponse.getStatusCode());
                    model.addAttribute("patientNotes", new ArrayList<Note>());
                    model.addAttribute("noteErrorMessage", "Erreur lors du chargement des notes (Code: " + notesResponse.getStatusCode() + ")");
                }
            } catch (Exception e) {
                log.error("Erreur inattendue lors de la récupération des notes pour ID {}", id, e);
                model.addAttribute("patientNotes", new ArrayList<Note>());
                model.addAttribute("noteErrorMessage", "Erreur inattendue (Notes).");
            }
            // Récupération Évaluation Risque Diabète
            try {

                ResponseEntity<String> diabetesResponse = restTemplate.exchange(
                        diabetesUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                if (diabetesResponse.getStatusCode() == HttpStatus.OK && diabetesResponse.getBody() != null) {
                    String riskLevelString = diabetesResponse.getBody();
                    log.info("Risque diabète reçu pour patient ID {}: {}", id, riskLevelString);
                    model.addAttribute("diabetesRisk", riskLevelString);
                } else {
                    log.warn("Erreur lors de la récupération de l'évaluation diabète pour patient ID {}, statut: {}", id, diabetesResponse.getStatusCode());
                    model.addAttribute("diabetesRisk", "Erreur évaluation");
                }
            } catch (Exception e) {
                log.error("Erreur inattendue lors de l'appel à l'évaluation diabète pour ID {}", id, e);
                model.addAttribute("diabetesRisk", "Erreur évaluation");
            }
        }
        return "patient-details";
    }
    @GetMapping("/patients/edit/{id}")
    public String showEditPatientForm(@PathVariable("id") int id, Model model) {
        String url = apiGatewayUrl + "/patients/" + id;

        try {

            String auth = backendApiUsername + ":" + backendApiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Patient> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Patient.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                model.addAttribute("patient", response.getBody());
                return "edit-patient-form";
            } else {
                model.addAttribute("errorMessage", "Patient non trouvé ou erreur API: " + response.getStatusCode());
                return "redirect:/ui/patients";
            }
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération pour modification, ID {}", id, e);
            model.addAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            return "redirect:/ui/patients";
        }
    }
    @GetMapping("/patients/add")
    public String showAddPatientForm(Model model) {

        // Crée un objet Patient vide pour le binding du formulaire
        model.addAttribute("patient", new Patient(0, "", "", "", "", "", ""));
        return "add-patient-form";
    }

    @GetMapping("/notes/add")
    public String showAddNoteForm(@RequestParam("patId") Integer patId, Model model) {
        String patientName = "Inconnu";
        String patientUrl = apiGatewayUrl + "/patients/" + patId;
        String auth = backendApiUsername + ":" + backendApiPassword;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Patient> patientResponse = restTemplate.exchange(
                    patientUrl,
                    HttpMethod.GET,
                    entity,
                    Patient.class
            );

            if (patientResponse.getStatusCode() == HttpStatus.OK && patientResponse.getBody() != null) {
                patientName = patientResponse.getBody().lastName();

            } else {
                log.warn("Impossible de récupérer les détails du patient ID {} pour le formulaire de note. Statut: {}", patId, patientResponse.getStatusCode());

            }
        } catch (RestClientException e) {
            log.error("Erreur API lors de la récupération du patient ID {} pour le formulaire de note: {}", patId, e.getMessage());
        }

        Note newNote = new Note(null, patId, patientName, null);
        model.addAttribute("note", newNote);
        model.addAttribute("patId", patId);

        return "add-note-form";
    }

    @PostMapping("/patients/save")
    public String savePatient(@ModelAttribute("patient") Patient patient, RedirectAttributes redirectAttributes) {
        String url = apiGatewayUrl + "/patients";

        try {
            String auth = backendApiUsername + ":" + backendApiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Crée l'entité de requête avec le patient dans le corps
            HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);

            // Appel POST à l'API
            ResponseEntity<Patient> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Patient.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                redirectAttributes.addFlashAttribute("successMessage", "Patient ajouté avec succès !");
                return "redirect:/ui/patients";
            } else {
                log.warn("Erreur lors de la création du patient via API, statut: {}", response.getStatusCode());
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout du patient (Statut: " + response.getStatusCode() + ")");
                return "add-patient-form";
            }

        } catch (Exception e) {
            log.error("Erreur inattendue lors de la sauvegarde du patient", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            return "redirect:/ui/patients/add";

        }
    }
    @PostMapping("/patients/update/{id}")
    public String updatePatient(@PathVariable("id") int id, @ModelAttribute("patient") Patient patient, RedirectAttributes redirectAttributes) {
        if (patient.id() == null || patient.id() != id) {
            log.warn("Incohérence d'ID lors de la mise à jour. Path ID: {}, Object ID: {}", id, patient.id());
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : Incohérence dans l'ID du patient.");
            return "redirect:/ui/patients";
        }

        String url = apiGatewayUrl + "/patients/" + id;
        log.debug("Appel API Gateway: PUT {}", url);

        try {

            String auth = backendApiUsername + ":" + backendApiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Crée l'entité de requête avec le patient mis à jour
            HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);

            // Appel PUT à l'API
            ResponseEntity<Patient> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Patient.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                redirectAttributes.addFlashAttribute("successMessage", "Patient mis à jour avec succès !");
                return "redirect:/ui/patients";
            } else {
                log.warn("Erreur lors de la mise à jour du patient ID {} via API, statut: {}", id, response.getStatusCode());
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du patient (Statut: " + response.getStatusCode() + ")");
                return "redirect:/ui/patients/edit/" + id;
            }
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour du patient ID {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            return "redirect:/ui/patients/edit/" + id;
        }
    }
    @PostMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        log.info("Requête pour supprimer le patient ID: {}", id);
        String url = apiGatewayUrl + "/patients/" + id;

        try {

            String auth = backendApiUsername + ":" + backendApiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Appel DELETE à l'API Gateway
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("Patient ID {} supprimé avec succès via API", id);
                redirectAttributes.addFlashAttribute("successMessage", "Patient supprimé avec succès !");
            } else {
                log.warn("Réponse inattendue lors de la suppression du patient ID {} via API, statut: {}", id, response.getStatusCode());
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du patient (Statut: " + response.getStatusCode() + ")");
            }

        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression du patient ID {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue s'est produite lors de la suppression.");
        }
        return "redirect:/ui/patients";
    }

    @PostMapping("/notes/save")
    public String saveNote(@ModelAttribute("note") Note note,
                           RedirectAttributes redirectAttributes) {

        // Vérifier si patId et note existent)
        if (note.patId() == null || note.note() == null || note.note().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "L'ID du patient et le contenu de la note sont requis.");
            return "redirect:/ui/notes/add?patId=" + (note.patId() != null ? note.patId() : "");
        }

        if (note.patient() == null || note.patient().trim().isEmpty()) {
            log.warn("Le nom du patient ('patient' field) est manquant dans la note soumise : {}. L'API backend risque de rejeter.", note);
        }
        String url = apiGatewayUrl + "/notes/create";


        try {
            String auth = backendApiUsername + ":" + backendApiPassword;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Note> requestEntity = new HttpEntity<>(note, headers);

            ResponseEntity<Note> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Note.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                redirectAttributes.addFlashAttribute("successMessage", "Note ajoutée avec succès !");

                return "redirect:/ui/patients/" + note.patId();
            } else {
                log.warn("Erreur lors de la création de la note via API pour patient ID {}, statut: {}", note.patId(), response.getStatusCode());
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout de la note (Statut: " + response.getStatusCode() + ")");
                return "redirect:/ui/notes/add?patId=" + note.patId();
            }

        } catch (Exception e) {
            log.error("Erreur inattendue lors de la sauvegarde de la note pour patient ID {}", note.patId(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            return "redirect:/ui/notes/add?patId=" + note.patId();
        }
    }
}
