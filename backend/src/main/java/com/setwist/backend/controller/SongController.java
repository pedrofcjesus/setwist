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

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.service.SongService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public List<SongResponseDTO> getAllSongs(Principal principal) {
        return songService.getAllSongsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDTO> getSongById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(songService.getSongByIdForUser(id, principal.getName()));
    }

    @PostMapping
    public ResponseEntity<SongResponseDTO> createSong(@Valid @RequestBody SongRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(songService.createSong(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongResponseDTO> updateSong(@PathVariable Long id, @Valid @RequestBody SongRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(songService.updateSong(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id, Principal principal) {
        songService.deleteSong(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}