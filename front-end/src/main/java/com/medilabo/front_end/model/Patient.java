package com.medilabo.front_end.model;

/**
 * Représentation d'un patient.
 *
 * @param id Identifiant unique du patient dans le système
 * @param lastName Nom de famille du patient
 * @param firstName Prénom du patient
 * @param birthdate Date de naissance du patient au format chaîne (généralement YYYY-MM-DD)
 * @param gender Genre du patient
 * @param address Adresse postale du patient
 * @param phoneNumber Numéro de téléphone du patient
 */
public record Patient(
        Integer id,
        String lastName,
        String firstName,
        String birthdate,
        String gender,
        String address,
        String phoneNumber
) {}