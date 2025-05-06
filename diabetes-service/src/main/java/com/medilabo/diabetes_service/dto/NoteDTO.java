package com.medilabo.diabetes_service.dto;


public class NoteDTO {
    private String id;
    private int patId;
    private String note;

    public NoteDTO() {
    }

    public NoteDTO(String id, int patId, String note) {
        this.id = id;
        this.patId = patId;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public int getPatId() {
        return patId;
    }

    public String getNote() {
        return note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPatId(int patId) {
        this.patId = patId;
    }

    public void setNote(String note) {
        this.note = note;
    }
}