package com.medilabo.diabetes_service.proxies;

import com.medilabo.diabetes_service.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "patient-service", url = "${patient.service.url}")
public interface PatientProxy {

    @GetMapping("/{id}")
    PatientDTO getPatientById(@PathVariable("id") Integer id);

    @GetMapping()
    List<PatientDTO> getAllPatients();
}