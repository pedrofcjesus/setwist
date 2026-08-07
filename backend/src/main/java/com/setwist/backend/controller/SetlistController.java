package com.setwist.backend.controller;

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
    public List<SetlistResponseDTO> getAllSetlists() {
        return setlistService.getAllSetlists();
    }

    @GetMapping("/band/{bandId}")
    public List<SetlistResponseDTO> getSetlistsByBand(@PathVariable Long bandId) {
        return setlistService.getSetlistsByBand(bandId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetlistResponseDTO> getSetlistById(@PathVariable Long id) {
        return ResponseEntity.ok(setlistService.getSetlistById(id));
    }

    @PostMapping("/band/{bandId}")
    public ResponseEntity<SetlistResponseDTO> createSetlistForBand(
            @PathVariable Long bandId,
            @Valid @RequestBody SetlistRequestDTO dto) {
        return ResponseEntity.ok(setlistService.createSetlistForBand(bandId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetlistResponseDTO> updateSetlist(
            @PathVariable Long id,
            @Valid @RequestBody SetlistRequestDTO dto) {
        return ResponseEntity.ok(setlistService.updateSetlist(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetlist(@PathVariable Long id) {
        setlistService.deleteSetlist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{setlistId}/songs/{songId}")
    public ResponseEntity<SetlistResponseDTO> addSongToSetlist(
            @PathVariable Long setlistId,
            @PathVariable Long songId) {
        return ResponseEntity.ok(setlistService.addSongToSetlist(setlistId, songId));
    }

    @DeleteMapping("/{setlistId}/songs/{songId}")
    public ResponseEntity<SetlistResponseDTO> removeSongFromSetlist(
            @PathVariable Long setlistId,
            @PathVariable Long songId) {
        return ResponseEntity.ok(setlistService.removeSongFromSetlist(setlistId, songId));
    }

    @PutMapping("/{setlistId}/reorder")
    public ResponseEntity<SetlistResponseDTO> reorderSetlist(
            @PathVariable Long setlistId,
            @RequestBody List<Long> newSongOrderIds) {
        return ResponseEntity.ok(setlistService.reorderSetlist(setlistId, newSongOrderIds));
    }
}