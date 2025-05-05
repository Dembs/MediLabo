package com.medilabo.front_end.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.front_end.model.Note;
import com.medilabo.front_end.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource; // Import TestPropertySource
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasProperty; // Import hamcrest matchers
import static org.hamcrest.Matchers.is;         // Import hamcrest matchers
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PatientUiController.class)
@TestPropertySource(properties = {
        "api.gateway.url=http://localhost:8080",
        "backend.api.username=testuser",
        "backend.api.password=testpassword"
})
class PatientUiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("${api.gateway.url}")
    private String apiGatewayUrl;
    @Value("${backend.api.username}")
    private String username;
    @Value("${backend.api.password}")
    private String password;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private HttpHeaders expectedHeaders;
    private HttpHeaders expectedPostPutHeaders;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Headers using the test properties
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        // Headers for GET/DELETE
        expectedHeaders = new HttpHeaders();
        expectedHeaders.set("Authorization", authHeader);
        expectedHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Headers for POST/PUT
        expectedPostPutHeaders = new HttpHeaders();
        expectedPostPutHeaders.set("Authorization", authHeader);
        expectedPostPutHeaders.setContentType(MediaType.APPLICATION_JSON);
        expectedPostPutHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }


    @Test
    @WithMockUser
    void listPatientsTest() throws Exception {
        Patient patient1 = new Patient(1, "Test", "Nom", "1980-01-01", "M", "1 rue", "12345");
        Patient patient2 = new Patient(2, "Test2", "Nom2", "1990-02-02", "F", "2 rue", "67890");
        List<Patient> expectedPatients = Arrays.asList(patient1, patient2);
        ResponseEntity<List<Patient>> mockResponse = ResponseEntity.ok(expectedPatients);

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/patients"),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(expectedHeaders)),
                any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        mockMvc.perform(get("/ui/patients"))
               .andExpect(status().isOk())
               .andExpect(view().name("patients"))
               .andExpect(model().attribute("patients", expectedPatients));

        verify(restTemplate).exchange(
                eq(apiGatewayUrl + "/patients"),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(expectedHeaders)),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @WithMockUser
    void viewPatientDetailsTest() throws Exception {
        int patientId = 1;
        Patient patient = new Patient(patientId, "Test", "Nom", "1980-01-01", "M", "1 rue", "12345");
        Note note1 = new Note("noteId1", patientId, "Nom", "Note 1 content");
        List<Note> notes = Collections.singletonList(note1);
        String riskLevel = "BORDERLINE";

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/patients/" + patientId),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(expectedHeaders)),
                eq(Patient.class))
        ).thenReturn(ResponseEntity.ok(patient));

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/notes/" + patientId),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(expectedHeaders)),
                any(ParameterizedTypeReference.class))
        ).thenReturn(ResponseEntity.ok(notes));

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/diabetes/" + patientId),
                eq(HttpMethod.GET),
                eq(new HttpEntity<>(expectedHeaders)),
                eq(String.class))
        ).thenReturn(ResponseEntity.ok(riskLevel));

        mockMvc.perform(get("/ui/patients/{id}", patientId))
               .andExpect(status().isOk())
               .andExpect(view().name("patient-details"))
               .andExpect(model().attribute("patient", patient))
               .andExpect(model().attribute("patientNotes", notes))
               .andExpect(model().attribute("diabetesRisk", riskLevel));

        verify(restTemplate).exchange(eq(apiGatewayUrl + "/patients/" + patientId), eq(HttpMethod.GET), eq(new HttpEntity<>(expectedHeaders)), eq(Patient.class));
        verify(restTemplate).exchange(eq(apiGatewayUrl + "/notes/" + patientId), eq(HttpMethod.GET), eq(new HttpEntity<>(expectedHeaders)), any(ParameterizedTypeReference.class));
        verify(restTemplate).exchange(eq(apiGatewayUrl + "/diabetes/" + patientId), eq(HttpMethod.GET), eq(new HttpEntity<>(expectedHeaders)), eq(String.class));
    }


    @Test
    @WithMockUser
    void showAddPatientFormTest() throws Exception {
        mockMvc.perform(get("/ui/patients/add"))
               .andExpect(status().isOk())
               .andExpect(view().name("add-patient-form"))
               .andExpect(model().attributeExists("patient"));
    }


    @Test
    @WithMockUser
    void savePatientTest() throws Exception {
        Patient newPatient = new Patient(null, "New", "Patient", "2000-05-10", "F", "123 Main St", "555-1234");
        Patient savedPatient = new Patient(5, "New", "Patient", "2000-05-10", "F", "123 Main St", "555-1234");

        HttpEntity<Patient> requestEntity = new HttpEntity<>(newPatient, expectedPostPutHeaders);
        ResponseEntity<Patient> mockResponse = ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/patients"),
                eq(HttpMethod.POST),
                eq(requestEntity),
                eq(Patient.class)
        )).thenReturn(mockResponse);

        mockMvc.perform(post("/ui/patients/save")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                       .flashAttr("patient", newPatient))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/ui/patients"))
               .andExpect(flash().attributeExists("successMessage"));

        verify(restTemplate).exchange(eq(apiGatewayUrl + "/patients"), eq(HttpMethod.POST), eq(requestEntity), eq(Patient.class));
    }

    @Test
    @WithMockUser
    void deletePatientTest() throws Exception {

        int patientId = 1;
        ResponseEntity<Void> mockResponse = ResponseEntity.ok().build();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/patients/" + patientId),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(mockResponse);

        mockMvc.perform(post("/ui/patients/delete/{id}", patientId)
                       .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/ui/patients"))
               .andExpect(flash().attributeExists("successMessage"));

        verify(restTemplate).exchange(
                eq(apiGatewayUrl + "/patients/" + patientId),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }

    @Test
    @WithMockUser
    void showAddNoteFormTest() throws Exception {
        int patId = 1;
        String expectedLastName = "LastName";
        Patient patient = new Patient(patId, "LastName", "FirstName", "1990-01-01", "M", "Addr", "Phone");

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/patients/" + patId), eq(HttpMethod.GET), eq(new HttpEntity<>(expectedHeaders)), eq(Patient.class))
        ).thenReturn(ResponseEntity.ok(patient));

        MvcResult result = mockMvc.perform(get("/ui/notes/add").param("patId", String.valueOf(patId)))
                                  .andExpect(status().isOk())
                                  .andExpect(view().name("add-note-form"))
                                  .andExpect(model().attributeExists("note"))
                                  .andExpect(model().attribute("patId", patId))
                                  .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertNotNull(mav);
        Note noteAttribute = (Note) mav.getModel().get("note");
        assertNotNull(noteAttribute);
        assertEquals(expectedLastName, noteAttribute.patient());

        verify(restTemplate).exchange(eq(apiGatewayUrl + "/patients/" + patId), eq(HttpMethod.GET), eq(new HttpEntity<>(expectedHeaders)), eq(Patient.class));
    }


    @Test
    @WithMockUser
    void saveNoteTest() throws Exception {
        int patId = 1;
        Note newNote = new Note(null, patId, "LastName", "This is a new note.");
        Note savedNote = new Note("noteId123", patId, "LastName", "This is a new note.");

        HttpEntity<Note> requestEntity = new HttpEntity<>(newNote, expectedPostPutHeaders);
        ResponseEntity<Note> mockResponse = ResponseEntity.status(HttpStatus.CREATED).body(savedNote);

        when(restTemplate.exchange(
                eq(apiGatewayUrl + "/notes/create"),
                eq(HttpMethod.POST),
                eq(requestEntity),
                eq(Note.class)
        )).thenReturn(mockResponse);

        mockMvc.perform(post("/ui/notes/save")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                       .flashAttr("note", newNote))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/ui/patients/" + patId))
               .andExpect(flash().attributeExists("successMessage"));

        verify(restTemplate).exchange(eq(apiGatewayUrl + "/notes/create"), eq(HttpMethod.POST), eq(requestEntity), eq(Note.class));
    }

}