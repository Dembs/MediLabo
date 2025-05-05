package com.medilabo.notes_service.repository;

import com.medilabo.notes_service.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès et la manipulation des notes médicales stockées dans MongoDB.
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Recherche toutes les notes médicales associées à un patient spécifique.
     *
     * @param patId L'identifiant du patient dont on souhaite récupérer les notes
     * @return Liste des notes médicales associées au patient
     */
    List<Note> findByPatId(Integer patId);

    /**
     * Supprime toutes les notes associées à un patientId donné.
     *
     * @param patId L'identifiant du patient dont les notes doivent être supprimées
     * @return Le nombre de notes supprimées
     */
    long deleteByPatId(Integer patId);
}