package com.medilabo.notes_service.data;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Captor
    private ArgumentCaptor<List<Note>> notesCaptor;

    @Test
    void dataTest() throws Exception {

        dataInitializer.run();


        verify(noteRepository).deleteAll();
        verify(noteRepository).saveAll(notesCaptor.capture());

        List<Note> capturedNotes = notesCaptor.getValue();
        assertNotNull(capturedNotes);
        assertFalse(capturedNotes.isEmpty());
    }
}
