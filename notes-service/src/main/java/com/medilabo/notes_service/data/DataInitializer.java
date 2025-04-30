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

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final NoteRepository noteRepository;

    public DataInitializer(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        noteRepository.deleteAll();

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Note>> typeReference = new TypeReference<List<Note>>() {};
        InputStream inputStream = new ClassPathResource("data/data.json").getInputStream();

        List<Map<String, Object>> rawNotes = mapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

        rawNotes.forEach(note -> {
            if (note.containsKey("_id")) {
                note.put("id", note.remove("_id"));
            }
        });

        List<Note> notes = mapper.convertValue(rawNotes, typeReference);
        noteRepository.saveAll(notes);

    }
}