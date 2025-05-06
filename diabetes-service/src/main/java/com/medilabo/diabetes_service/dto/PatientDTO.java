package com.medilabo.diabetes_service.dto;


import java.time.LocalDate;


public class PatientDTO {
    private int id;
    private LocalDate birthdate;
    private String gender;


    public PatientDTO() {
    }

    public PatientDTO(int id, LocalDate birthdate, String gender) {
        this.id = id;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}