package com.medilabo.notes_service.controller;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private Note testNote;

    @BeforeEach
    void setUp() {
        testNote = new Note(1, "Test Patient", "Contenu de test");
        testNote.setId("testId");
    }

    @Test
    void getNotesByPatientIdTest() {

        List<Note> expectedNotes = Arrays.asList(testNote);
        when(noteService.findByPatientId(1)).thenReturn(expectedNotes);

        ResponseEntity<List<Note>> response = noteController.getNotesByPatientId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNotes, response.getBody());
        verify(noteService).findByPatientId(1);
    }

    @Test
    void createNoteTest() {

        Note inputNote = new Note(1, "Test Patient", "Nouvelle note");
        when(noteService.createNote(inputNote)).thenReturn(testNote);

        ResponseEntity<Note> response = noteController.createNote(inputNote);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testNote, response.getBody());
        verify(noteService).createNote(inputNote);
    }

    @Test
    void deleteNotesByPatientIdTest() {

        doNothing().when(noteService).deleteNotesByPatientId(1);

        ResponseEntity<Void> response = noteController.deleteNotesByPatientId(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(noteService).deleteNotesByPatientId(1);
    }

}