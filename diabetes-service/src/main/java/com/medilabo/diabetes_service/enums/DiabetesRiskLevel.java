package com.medilabo.diabetes_service.enums;

/**
 * Énumération définissant les différents niveaux de risque de diabète.
 */
public enum DiabetesRiskLevel {
    NONE("Aucun risque"),
    BORDERLINE("Risque limité"),
    IN_DANGER("Danger"),
    EARLY_ONSET("Apparition précoce");

    private final String displayName;
    DiabetesRiskLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}