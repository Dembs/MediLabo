package com.medilabo.diabetes_service.proxies;

import com.medilabo.diabetes_service.dto.NoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "notes-service", url = "${note.service.url}")
public interface NoteProxy {
    @DeleteMapping("/{patId}")
    void deleteNotesForPatient(@PathVariable("patId") Integer patId);

    @GetMapping("/{patId}")
    List<NoteDTO> getNotesByPatientId(@PathVariable("patId") Integer patId);
}
