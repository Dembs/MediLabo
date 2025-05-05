package com.medilabo.patient_service.service;

import com.medilabo.patient_service.model.Patient;
import com.medilabo.patient_service.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PatientService patientService;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        patient1 = new Patient("Dupont", "Jean", "1980-01-15", "M", "1 Rue Test", "111-222-3333");
        patient1.setId(1);
        patient2 = new Patient("Durand", "Marie", "1990-05-20", "F", "2 Avenue Essai", "444-555-6666");
        patient2.setId(2);

        ReflectionTestUtils.setField(patientService, "apiGatewayUrl", "http://fake-gateway:8080");
    }

    @Test
    void getAllPatientsTest() {
        List<Patient> expectedPatients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(expectedPatients);

        List<Patient> actualPatients = patientService.getAllPatients();

        assertNotNull(actualPatients);
        assertEquals(2, actualPatients.size());
        assertEquals("Dupont", actualPatients.get(0).getLastName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientByIDTest() {
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient1));

        Patient actualPatient = patientService.getPatient(1);

        assertNotNull(actualPatient);
        assertEquals(1, actualPatient.getId());
        assertEquals("Dupont", actualPatient.getLastName());
        verify(patientRepository, times(1)).findById(1);
    }



    @Test
    void createPatientTest() {

        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        Patient newPatient = new Patient("Test", "Nouveau", "2000-10-10", "M", "Adresse", "000");
        Patient savedPatient = patientService.createPatient(newPatient);

        assertNotNull(savedPatient);
        assertEquals(1, savedPatient.getId());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatientTest() {
        Patient updatedInfo = new Patient("Dupont", "Jean-Pierre", "1980-01-15", "M", "Nouvelle Adresse", "999-888-7777");
        Patient patientExistantAvecUpdate = new Patient("Dupont", "Jean-Pierre", "1980-01-15", "M", "Nouvelle Adresse", "999-888-7777");
        patientExistantAvecUpdate.setId(1);

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient1));
        when(patientRepository.save(any(Patient.class))).thenReturn(patientExistantAvecUpdate);

        Patient result = patientService.updatePatient(updatedInfo, 1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Jean-Pierre", result.getFirstName());
        assertEquals("Nouvelle Adresse", result.getAddress());
        assertEquals("999-888-7777", result.getPhoneNumber());

        assertEquals("1980-01-15", result.getBirthdate());
        assertEquals("M", result.getGender());

        verify(patientRepository, times(1)).findById(1);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }


    @Test
    void deletePatientTest() {
        int patientId = 1;
        when(patientRepository.existsById(patientId)).thenReturn(true);
        ResponseEntity<Void> responseOk = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(
                eq("http://fake-gateway:8080/notes/" + patientId),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(responseOk);

        boolean result = patientService.deletePatient(patientId);

        assertTrue(result);
        verify(patientRepository, times(1)).existsById(patientId);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));
        verify(patientRepository, times(1)).deleteById(patientId);
    }
}