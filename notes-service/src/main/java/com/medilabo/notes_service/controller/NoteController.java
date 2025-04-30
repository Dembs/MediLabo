package com.medilabo.notes_service.controller;


import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;

    @GetMapping("/notes/{patId}")
    public ResponseEntity<List<Note>> getNotesByPatientId(@PathVariable Integer patId) {
        List<Note> notes = noteService.findByPatientId(patId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/notes/create")
    public ResponseEntity<Note> createNote(@RequestBody Note newNote) {
        Note note = noteService.createNote(newNote);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    /**
     * Endpoint pour supprimer toutes les notes d'un patient spécifique.
     * Appelée par le patient-service lors de la suppression d'un patient.
     *
     * @param patId L'ID du patient dont les notes doivent être supprimées.
     * @return ResponseEntity avec statut No Content (204) si succès, ou erreur sinon.
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
