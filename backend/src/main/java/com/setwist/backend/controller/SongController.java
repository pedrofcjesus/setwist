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

import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.model.Song;
import com.setwist.backend.service.SongService;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public List<SongResponseDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/band/{bandId}")
    public List<SongResponseDTO> getSongsByBand(@PathVariable Long bandId) {
        return songService.getSongsByBand(bandId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDTO> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @PostMapping("/band/{bandId}")
    public ResponseEntity<SongResponseDTO> createSongForBand(@PathVariable Long bandId, @RequestBody Song song) {
        return ResponseEntity.ok(songService.createSongForBand(bandId, song));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongResponseDTO> updateSong(@PathVariable Long id, @RequestBody Song songDetails) {
        return ResponseEntity.ok(songService.updateSong(id, songDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}
