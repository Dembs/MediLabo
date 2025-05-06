package com.medilabo.front_end.model;

public record PatientListDTO(
        Integer id,
        String lastName,
        String firstName,
        String birthdate,
        String gender,
        Integer age,
        String riskLevel
) {}
