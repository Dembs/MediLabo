package com.medilabo.diabetes_service.service;

import com.medilabo.diabetes_service.dto.NoteDTO;
import com.medilabo.diabetes_service.dto.PatientDTO;
import com.medilabo.diabetes_service.enums.DiabetesRiskLevel;
import com.medilabo.diabetes_service.proxies.NoteProxy;
import com.medilabo.diabetes_service.proxies.PatientProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiabetesServiceTest {

    @Mock
    private PatientProxy patientProxy;

    @Mock
    private NoteProxy noteProxy;

    @InjectMocks
    private DiabetesService diabetesService;



    @Test
    void assessDiabetesRisk_NONETest() {
        PatientDTO patient = new PatientDTO();
        patient.setId(1);
        patient.setGender("M");
        patient.setBirthdate(LocalDate.now().minusYears(40));
        when(patientProxy.getPatientById(1)).thenReturn(patient);

        when(noteProxy.getNotesByPatientId(1)).thenReturn(Collections.emptyList());

        DiabetesRiskLevel result = diabetesService.assessDiabetesRisk(1);

        assertEquals(DiabetesRiskLevel.NONE, result);
        verify(patientProxy).getPatientById(1);
        verify(noteProxy).getNotesByPatientId(1);
    }

    @Test
    void assessDiabetesRisk_InDangerTest() {
        PatientDTO patient = new PatientDTO();
        patient.setId(1);
        patient.setGender("M");
        patient.setBirthdate(LocalDate.now().minusYears(25));

        NoteDTO note1 = new NoteDTO();
        note1.setNote("Patient presente des symptomes d'hémoglobine a1c");
        NoteDTO note2 = new NoteDTO();
        note2.setNote("Patient a microalbumine and problèmes de poids ");
        NoteDTO note3 = new NoteDTO();
        note3.setNote("Patient montre des signes of vertige");
        List<NoteDTO> notes = Arrays.asList(note1, note2, note3);

        when(patientProxy.getPatientById(1)).thenReturn(patient);
        when(noteProxy.getNotesByPatientId(1)).thenReturn(notes);

        DiabetesRiskLevel result = diabetesService.assessDiabetesRisk(1);

        assertEquals(DiabetesRiskLevel.IN_DANGER, result);
        verify(patientProxy).getPatientById(1);
        verify(noteProxy).getNotesByPatientId(1);
    }

    @Test
    void assessDiabetesRisk_EarlyOnsetTest() {
        PatientDTO patient = new PatientDTO();
        patient.setId(2);
        patient.setGender("F");
        patient.setBirthdate(LocalDate.now().minusYears(28));

        NoteDTO note = new NoteDTO();
        note.setNote("Patient montre des signes d'hémoglobine a1c, microalbumine, taille, poids, fumeur, anormal, cholestérol, vertige");
        List<NoteDTO> notes = Collections.singletonList(note);

        when(patientProxy.getPatientById(2)).thenReturn(patient);
        when(noteProxy.getNotesByPatientId(2)).thenReturn(notes);

        DiabetesRiskLevel result = diabetesService.assessDiabetesRisk(2);

        assertEquals(DiabetesRiskLevel.EARLY_ONSET, result);
        verify(patientProxy).getPatientById(2);
        verify(noteProxy).getNotesByPatientId(2);
    }

    @Test
    void assessDiabetesRisk_BorderlineTest() {
        PatientDTO patient = new PatientDTO();
        patient.setId(3);
        patient.setGender("M");
        patient.setBirthdate(LocalDate.now().minusYears(55));

        NoteDTO note = new NoteDTO();
        note.setNote("Patient montre des signes de cholestérol and poids");
        List<NoteDTO> notes = Collections.singletonList(note);

        when(patientProxy.getPatientById(3)).thenReturn(patient);
        when(noteProxy.getNotesByPatientId(3)).thenReturn(notes);

        DiabetesRiskLevel result = diabetesService.assessDiabetesRisk(3);

        assertEquals(DiabetesRiskLevel.BORDERLINE, result);
        verify(patientProxy).getPatientById(3);
        verify(noteProxy).getNotesByPatientId(3);
    }
}