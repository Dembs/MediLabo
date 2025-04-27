package com.medilabo.front_end.controller;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
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

    @GetMapping("/patients")
    public String listPatients(Model model) {

        String url = apiGatewayUrl + "/patients"; // Construire l'URL complète de l'API via la gateway

        try {
            // Création de l'en-tête d'authentification Basic
            String auth = "user" + ":" + "password";
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // On attend du JSON

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Appel à l'API via RestTemplate
            ResponseEntity<List<Patient>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Patient>>() {} //ParameterizedTypeReference pour gérer correctement la liste générique List<Patient>
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                model.addAttribute("patients", response.getBody());
            } else {
                log.warn("Réponse non OK reçue de l'API Gateway : {}", response.getStatusCode());
                model.addAttribute("errorMessage", "Impossible de récupérer la liste des patients. Statut : " + response.getStatusCode());
                model.addAttribute("patients", Collections.emptyList());
            }

        } catch (HttpClientErrorException.Unauthorized unauthorized) {
            log.error("Erreur 401 Unauthorized lors de l'appel à l'API Gateway à l'URL: {}. Vérifiez les identifiants.", url, unauthorized);
            model.addAttribute("errorMessage", "Erreur d'authentification lors de l'appel à l'API. Vérifiez les identifiants dans le code et la configuration de sécurité.");
            model.addAttribute("patients", Collections.emptyList());
        } catch (RestClientException e) {
            log.error("Erreur RestClient lors de l'appel à l'API Gateway à l'URL: {}", url, e);
            model.addAttribute("errorMessage", "Erreur de communication avec l'API Gateway : " + e.getMessage());
            model.addAttribute("patients", Collections.emptyList());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des patients UI", e);
            model.addAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            model.addAttribute("patients", Collections.emptyList());
        }

        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String viewPatient(@PathVariable("id") int id, Model model) {
        String url = apiGatewayUrl + "/patients/" + id;

        try {

            String auth = "user" + ":" + "password";
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
            } else {
                model.addAttribute("errorMessage", "Patient non trouvé ou erreur API: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.NotFound notFound) {
            log.warn("Patient non trouvé (404) pour ID: {}", id);
            model.addAttribute("errorMessage", "Patient avec ID " + id + " non trouvé.");
        } catch (RestClientException e) {
            log.error("Erreur RestClient lors de l'appel à GET {}: {}", url, e.getMessage());
            model.addAttribute("errorMessage", "Erreur de communication avec l'API Gateway.");
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération du patient ID {}", id, e);
            model.addAttribute("errorMessage", "Une erreur inattendue s'est produite.");
        }
        return "patient-details";
    }
    @GetMapping("/patients/edit/{id}")
    public String showEditPatientForm(@PathVariable("id") int id, Model model) {
        String url = apiGatewayUrl + "/patients/" + id;

        try {

            String auth = "user" + ":" + "password";
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
        } catch (HttpClientErrorException.NotFound notFound) {
            log.warn("Patient non trouvé (404) pour modification, ID: {}", id);
            model.addAttribute("errorMessage", "Patient avec ID " + id + " non trouvé.");
            return "redirect:/ui/patients";
        } catch (RestClientException e) {
            log.error("Erreur RestClient lors de la récupération pour modification {}: {}", url, e.getMessage());
            model.addAttribute("errorMessage", "Erreur de communication avec l'API Gateway.");
            return "redirect:/ui/patients";
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération pour modification, ID {}", id, e);
            model.addAttribute("errorMessage", "Une erreur inattendue s'est produite.");
            return "redirect:/ui/patients";
        }
    }
    @GetMapping("/patients/add")
    public String showAddPatientForm(Model model) {

        // Crée un objet Patient vide pour le binding du formulaire
        model.addAttribute("patient", new Patient(0, "", "", "", "", "", "")); // ID 0 ou null si type Long
        return "add-patient-form";
    }

    @PostMapping("/patients/save")
    public String savePatient(@ModelAttribute("patient") Patient patient, RedirectAttributes redirectAttributes) {
        String url = apiGatewayUrl + "/patients";

        try {
            String auth = "user" + ":" + "password";
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

        } catch (RestClientException e) {
            log.error("Erreur RestClient lors de l'appel à POST {}: {}", url, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur de communication avec l'API Gateway.");
            return "redirect:/ui/patients/add";

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

            String auth = "user" + ":" + "password";
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

        } catch (RestClientException e) {
            log.error("Erreur RestClient lors de l'appel à PUT {}: {}", url, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur de communication avec l'API Gateway.");
            return "redirect:/ui/patients/edit/" + id;
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
        log.debug("Appel API Gateway: DELETE {}", url);

        try {

            String auth = "user" + ":" + "password";
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

        } catch (HttpClientErrorException.NotFound notFound) {
            log.warn("Tentative de suppression d'un patient non trouvé (404), ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Patient avec ID " + id + " non trouvé. Il a peut-être déjà été supprimé.");
        } catch (RestClientException e) {
            log.error("Erreur RestClient lors de l'appel à DELETE {}: {}", url, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur de communication avec l'API Gateway lors de la suppression.");
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression du patient ID {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur inattendue s'est produite lors de la suppression.");
        }

        return "redirect:/ui/patients";
    }
}
