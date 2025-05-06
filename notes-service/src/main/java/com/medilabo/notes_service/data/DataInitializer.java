package com.medilabo.notes_service.data;

import com.fasterxml.jackson.core.type.TypeReference; // Important
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Initialise la base de données des notes médicales avec des données d'exemple au démarrage de l'application.
 * Cette classe charge un fichier JSON contenant des notes pré-définies, les transforme en objets Note
 * et les enregistre dans la base de données MongoDB.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final NoteRepository noteRepository;

    public DataInitializer(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Méthode exécutée au démarrage de l'application pour initialiser les données.
     * Supprime d'abord toutes les notes existantes, puis charge les nouvelles notes
     * à partir du fichier JSON de données d'exemple.
     *
     * @param args Arguments de ligne de commande (non utilisés)
     * @throws Exception Si une erreur survient pendant le chargement des données
     */
    @Override
    public void run(String... args) throws Exception {
        // Supprime toutes les notes existantes pour garantir un état propre
        noteRepository.deleteAll();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Note>> typeReference = new TypeReference<List<Note>>() {};
        InputStream inputStream = new ClassPathResource("data/data.json").getInputStream();

        // Charge d'abord en tant que List<Map> pour pouvoir manipuler les clés
        List<Map<String, Object>> rawNotes = mapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

        // Conversion de "_id" en "id" pour assurer la compatibilité avec le modèle Java
        rawNotes.forEach(note -> {
            if (note.containsKey("_id")) {
                note.put("id", note.remove("_id"));
            }
        });

        // Convertit les maps en objets Note
        List<Note> notes = mapper.convertValue(rawNotes, typeReference);
        noteRepository.saveAll(notes);
    }
}