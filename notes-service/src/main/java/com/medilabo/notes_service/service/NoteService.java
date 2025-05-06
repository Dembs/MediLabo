package com.medilabo.notes_service.service;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant les fonctionnalités nécessaires pour créer, récupérer et supprimer
 * des notes médicales associées aux patients. Il sert d'intermédiaire entre le contrôleur
 * REST et le repository MongoDB.
 */
@Service
public class NoteService {
    private static final Logger logger = LogManager.getLogger(NoteService.class);

    @Autowired
    private NoteRepository noteRepository;

    /**
     * Recherche toutes les notes médicales associées à un patient spécifique.
     *
     * @param patId L'identifiant du patient dont on souhaite récupérer les notes
     * @return Liste des notes médicales associées au patient
     */
    public List<Note> findByPatientId(Integer patId) {
        return noteRepository.findByPatId(patId);
    }

    /**
     * Crée une nouvelle note médicale
     *
     * @param newNote L'objet Note contenant les informations de la nouvelle note
     * @return La note créée avec son identifiant généré
     */
    public Note createNote(Note newNote) {
        Note savedNote = noteRepository.save(newNote);
        logger.info("Successfully created note for the patient {}", savedNote.getPatient());
        return savedNote;
    }

    /**
     * Supprime toutes les notes médicales associées à un patient spécifique.
     * Cette méthode est utilisée lors de la suppression d'un patient pour maintenir
     * la cohérence des données entre les microservices.
     *
     * @param patId L'identifiant du patient dont les notes doivent être supprimées
     */
    @Transactional
    public void deleteNotesByPatientId(Integer patId) {
        long deletedCount = noteRepository.deleteByPatId(patId);
        logger.info("Suppression de {} note(s) pour le patient ID {}", deletedCount, patId);
    }
}