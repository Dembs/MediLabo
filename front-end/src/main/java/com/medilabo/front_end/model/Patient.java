package com.medilabo.front_end.model;

// Record Java pour un DTO simple et immuable
public record Patient(
        Integer id,
        String lastName,
        String firstName,
        String birthdate,
        String gender,
        String address,
        String phoneNumber
) {}