package com.medilabo.front_end.model;

/**
 * Représentation d'une note médicale associée à un patient.
 *
 * @param id Identifiant unique de la note
 * @param patId Identifiant du patient auquel la note est associée
 * @param patient Nom du patient (généralement le nom de famille)
 * @param note Contenu textuel de la note médicale
 */
public record Note(
        String id,
        Integer patId,
        String patient,
        String note
) {}