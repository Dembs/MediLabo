package com.medilabo.notes_service.controller;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints pour la gestion des notes médicales associées aux patients.
 */
@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;

    /**
     * Récupère toutes les notes médicales associées à un patient spécifique.
     *
     * @param patId Identifiant du patient dont on souhaite récupérer les notes
     * @return Liste des notes médicales du patient
     */
    @GetMapping("/notes/{patId}")
    public ResponseEntity<List<Note>> getNotesByPatientId(@PathVariable Integer patId) {
        List<Note> notes = noteService.findByPatientId(patId);
        return ResponseEntity.ok(notes);
    }

    /**
     * Crée une nouvelle note médicale pour un patient.
     *
     * @param newNote Objet Note contenant les informations de la nouvelle note
     * @return La note créée avec ses informations complètes
     */
    @PostMapping("/notes/create")
    public ResponseEntity<Note> createNote(@RequestBody Note newNote) {
        Note note = noteService.createNote(newNote);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    /**
     * Endpoint pour supprimer toutes les notes d'un patient spécifique.
     *
     * @param patId L'ID du patient dont les notes doivent être supprimées
     * @return ResponseEntity avec statut 204 (No Content) si succès, ou 500 (Internal Server Error) en cas d'échec
     */
    @DeleteMapping("/notes/{patId}")
    public ResponseEntity<Void> deleteNotesByPatientId(@PathVariable Integer patId) {
        try {
            noteService.deleteNotesByPatientId(patId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}