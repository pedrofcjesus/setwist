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

    // 1. READ ALL
    @GetMapping
    public List<SongResponseDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    // 2. READ BY BAND
    @GetMapping("/band/{bandId}")
    public List<SongResponseDTO> getSongsByBand(@PathVariable Long bandId) {
        return songService.getSongsByBand(bandId);
    }

    // 3. READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDTO> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    // 4. CREATE FOR BAND
    @PostMapping("/band/{bandId}")
    public ResponseEntity<SongResponseDTO> createSongForBand(
            @PathVariable Long bandId,
            @Valid @RequestBody SongRequestDTO dto) {
        return ResponseEntity.ok(songService.createSongForBand(bandId, dto));
    }

    // 5. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SongResponseDTO> updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequestDTO dto) {
        return ResponseEntity.ok(songService.updateSong(id, dto));
    }

    // 6. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}