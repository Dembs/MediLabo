package com.medilabo.notes_service.service;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {
    private static final Logger logger = LogManager.getLogger(NoteService.class);

    @Autowired
    private NoteRepository noteRepository;

    public List<Note> findByPatientId(Integer patId) {
        return noteRepository.findByPatId(patId);
    }

    public Note createNote(Note newNote) {
        Note savedNote = noteRepository.save(newNote);
        logger.info("Successfully created note for the patient {}", savedNote.getPatient());
        return savedNote;
    }
    /**
     * Supprime toutes les notes pour un patient spécifique.
     *
     * @param patId L'identifiant du patient.
     */
    @Transactional
    public void deleteNotesByPatientId(Integer patId) {
        long deletedCount = noteRepository.deleteByPatId(patId);
    }

}