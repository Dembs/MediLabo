package com.medilabo.front_end.model;

import java.time.LocalDateTime;

public record Note(
        String id,
        Integer patId,
        String patient,
        String note
) {}