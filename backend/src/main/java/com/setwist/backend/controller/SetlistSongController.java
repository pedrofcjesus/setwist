package com.setwist.backend.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongReorderDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.service.SetlistSongService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/setlists/{setlistId}/songs")
public class SetlistSongController {

    private final SetlistSongService setlistSongService;

    public SetlistSongController(SetlistSongService setlistSongService) {
        this.setlistSongService = setlistSongService;
    }

    @PostMapping
    public ResponseEntity<SetlistResponseDTO> addSong(
            @PathVariable Long setlistId,
            @Valid @RequestBody SetlistSongRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.addSongToSetlist(setlistId, dto, principal.getName()));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<SetlistResponseDTO> removeSong(
            @PathVariable Long setlistId,
            @PathVariable Long songId,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.removeSongFromSetlist(setlistId, songId, principal.getName()));
    }

    @PutMapping("/reorder")
    public ResponseEntity<SetlistResponseDTO> reorderSongs(
            @PathVariable Long setlistId,
            @Valid @RequestBody SetlistSongReorderDTO dto,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.reorderSongs(setlistId, dto, principal.getName()));
    }
}