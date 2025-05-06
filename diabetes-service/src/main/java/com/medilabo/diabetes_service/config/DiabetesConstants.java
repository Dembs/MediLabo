package com.medilabo.diabetes_service.config;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/**
 * Classe utilitaire définissant les constantes utilisées par le service d'évaluation du diabète.
 * Contient notamment la liste des termes médicaux considérés comme des déclencheurs
 * pour l'évaluation des risques de diabète.
 * Cette classe n'est pas instanciable
 */
public final class DiabetesConstants {

    /**
     * Liste immuable des termes médicaux qui, lorsqu'ils sont trouvés dans les notes
     * médicales d'un patient, contribuent à l'évaluation du risque de diabète.
     * Chaque occurrence de ces termes dans les notes d'un patient est comptée comme
     * un facteur de risque potentiel pour le diabète.
     */
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

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     *
     * @throws IllegalStateException si tentative d'instanciation
     */
    private DiabetesConstants() {
        throw new IllegalStateException("Utility class");
    }
}