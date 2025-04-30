package com.medilabo.notes_service.repository;

import com.medilabo.notes_service.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByPatId(Integer patId);
    /**
     * Supprime toutes les notes associées à un patientId donné.
     *
     * @param patId L'identifiant du patient dont les notes doivent être supprimées.
     */
    long deleteByPatId(Integer patId);
}