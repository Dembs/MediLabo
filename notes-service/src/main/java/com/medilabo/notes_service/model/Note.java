package com.medilabo.notes_service.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "notes")
public class Note {
    @Id
    private String id;
    private Integer patId;
    private String patient;
    private String note;

    public Note() {
    }

    public Note(Integer patId, String patient, String note) {
        this.patId = patId;
        this.patient = patient;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public Integer getPatId() {
        return patId;
    }

    public String getPatient() {
        return patient;
    }

    public String getNote() {
        return note;
    }



    public void setId(String id) {
        this.id = id;
    }

    public void setPatId(Integer patId) {
        this.patId = patId;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setNote(String note) {
        this.note = note;
    }

}