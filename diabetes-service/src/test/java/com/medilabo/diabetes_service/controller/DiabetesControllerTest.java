
package com.medilabo.diabetes_service.controller;

import com.medilabo.diabetes_service.enums.DiabetesRiskLevel;
import com.medilabo.diabetes_service.service.DiabetesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiabetesControllerTest {

    @Mock
    private DiabetesService diabetesService;

    @InjectMocks
    private DiabetesController diabetesController;

    @Test
    void getDiabetesAssessment_NoneTest() {
        when(diabetesService.assessDiabetesRisk(1)).thenReturn(DiabetesRiskLevel.NONE);

        DiabetesRiskLevel result = diabetesController.getDiabetesAssessment(1);

        assertEquals(DiabetesRiskLevel.NONE, result);
        verify(diabetesService).assessDiabetesRisk(1);
    }

    @Test
    void getDiabetesAssessment_BorderlineTest() {

        when(diabetesService.assessDiabetesRisk(2)).thenReturn(DiabetesRiskLevel.BORDERLINE);

        DiabetesRiskLevel result = diabetesController.getDiabetesAssessment(2);

        assertEquals(DiabetesRiskLevel.BORDERLINE, result);
        verify(diabetesService).assessDiabetesRisk(2);
    }

    @Test
    void getDiabetesAssessment_InDangerTest() {
        when(diabetesService.assessDiabetesRisk(3)).thenReturn(DiabetesRiskLevel.IN_DANGER);

        DiabetesRiskLevel result = diabetesController.getDiabetesAssessment(3);

        assertEquals(DiabetesRiskLevel.IN_DANGER, result);
        verify(diabetesService).assessDiabetesRisk(3);
    }

    @Test
    void getDiabetesAssessment_EarlyOnsetTest() {
        when(diabetesService.assessDiabetesRisk(4)).thenReturn(DiabetesRiskLevel.EARLY_ONSET);

        DiabetesRiskLevel result = diabetesController.getDiabetesAssessment(4);

        assertEquals(DiabetesRiskLevel.EARLY_ONSET, result);
        verify(diabetesService).assessDiabetesRisk(4);
    }

}