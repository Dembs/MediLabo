package com.medilabo.patient_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Le nom de famille est obligatoire")
    private String lastName;
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;
    @NotBlank(message = "La date de naissance est obligatoire")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Format de date incorrect (YYYY-MM-DD)")
    private String birthdate;
    @NotBlank(message = "Le genre est obligatoire")
    @Pattern(regexp = "^[MF]$", message = "Le genre doit être 'M' ou 'F'")
    private String gender;
    private String address;
    private String phoneNumber;

    public Patient(){}
    public Patient(String lastName, String firstName, String birthdate, String gender, String address, String phoneNumber) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
