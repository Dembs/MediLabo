package com.medilabo.notes_service.service;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note testNote;

    @BeforeEach
    void setUp() {
        testNote = new Note(1, "Test Patient", "Contenu de test");
        testNote.setId("testId");
    }

    @Test
    void findByPatientIdTest() {

        List<Note> expectedNotes = Arrays.asList(testNote);
        when(noteRepository.findByPatId(1)).thenReturn(expectedNotes);

        List<Note> actualNotes = noteService.findByPatientId(1);

        assertEquals(expectedNotes, actualNotes);
        verify(noteRepository).findByPatId(1);
    }

    @Test
    void createNoteTest() {

        Note inputNote = new Note(1, "Test Patient", "Nouvelle note");
        when(noteRepository.save(inputNote)).thenReturn(testNote);

        Note createdNote = noteService.createNote(inputNote);

        assertEquals(testNote, createdNote);
        verify(noteRepository).save(inputNote);
    }

    @Test
    void deleteNotesByPatientIdTest() {
        when(noteRepository.deleteByPatId(1)).thenReturn(2L);

        noteService.deleteNotesByPatientId(1);

        verify(noteRepository).deleteByPatId(1);
    }
}