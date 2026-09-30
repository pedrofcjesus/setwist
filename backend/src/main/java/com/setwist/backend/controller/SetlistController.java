package com.setwist.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.service.SetlistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/setlists")
public class SetlistController {

    private final SetlistService setlistService;

    public SetlistController(SetlistService setlistService) {
        this.setlistService = setlistService;
    }

    @GetMapping
    public List<SetlistResponseDTO> getAllSetlists(Principal principal) {
        return setlistService.getAllSetlistsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetlistResponseDTO> getSetlistById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(setlistService.getSetlistByIdForUser(id, principal.getName()));
    }

    @PostMapping
    public ResponseEntity<SetlistResponseDTO> createSetlist(@Valid @RequestBody SetlistRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(setlistService.createSetlist(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetlistResponseDTO> updateSetlist(@PathVariable Long id, @Valid @RequestBody SetlistRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(setlistService.updateSetlist(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetlist(@PathVariable Long id, Principal principal) {
        setlistService.deleteSetlist(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}