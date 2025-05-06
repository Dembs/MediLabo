package com.medilabo.patient_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {

        patient1 = new Patient("Dupont", "Jean", LocalDate.parse("1980-01-15"), "M", "1 Rue Test", "111-222-3333");
        patient1.setId(1);
        patient2 = new Patient("Durand", "Marie", LocalDate.parse("1990-05-20"), "F", "2 Avenue Essai", "444-555-6666");
        patient2.setId(2);
    }

    @Test
    void getAllPatientsTest() throws Exception {
        List<Patient> patientsList = Arrays.asList(patient1, patient2);
        when(patientService.getAllPatients()).thenReturn(patientsList);

        mockMvc.perform(get("/patients"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].lastName", is("Dupont")))
               .andExpect(jsonPath("$[1].lastName", is("Durand")));

        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    void getPatientByIdTest() throws Exception {
        when(patientService.getPatient(1)).thenReturn(patient1);

        mockMvc.perform(get("/patients/{id}", 1))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.lastName", is("Dupont")));

        verify(patientService, times(1)).getPatient(1);
    }


    @Test
    void createPatientTest() throws Exception {
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient1);

        mockMvc.perform(post("/patients")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(patient1)))
               .andExpect(status().isCreated())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.lastName", is("Dupont")));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    void updatePatientTest() throws Exception {
        Patient updatedInfo = new Patient("Dupont", "Jean-Pierre", LocalDate.parse("1980-01-15"), "M", "1 Rue Test Modifiée", "111-222-4444");
        when(patientService.updatePatient(any(Patient.class), eq(1))).thenReturn(patient1);


        mockMvc.perform(put("/patients/{id}", 1)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(updatedInfo)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(1)));

        verify(patientService, times(1)).updatePatient(any(Patient.class), eq(1));
    }

    @Test
    void deletePatientTest() throws Exception {
        when(patientService.deletePatient(1)).thenReturn(true);

        mockMvc.perform(delete("/patients/{id}", 1))
               .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1);
    }

}