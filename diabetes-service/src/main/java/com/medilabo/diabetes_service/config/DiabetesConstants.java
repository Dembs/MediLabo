package com.medilabo.diabetes_service.config;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public final class DiabetesConstants {

    public static final List<String> TRIGGER_TERMS = Collections.unmodifiableList(Arrays.asList(
            "hémoglobine a1c",
            "microalbumine",
            "taille",
            "poids",
            "fumeur",
            "fumeuse",
            "fumer",
            "anormal",
            "cholestérol",
            "vertiges",
            "vertiges",
            "rechute",
            "réaction",
            "réactions",
            "anticorps"
    ));

    private DiabetesConstants() {
        throw new IllegalStateException("Utility class");
    }
}
