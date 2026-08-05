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
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongRepository songRepository;
    private final BandRepository bandRepository;

    public SongController(SongRepository songRepository, BandRepository bandRepository) {
        this.songRepository = songRepository;
        this.bandRepository = bandRepository;
    }

    // 1. READ ALL (Retorna DTOs de todas as músicas)
    @GetMapping
    public List<SongResponseDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(SongResponseDTO::new)
                .toList();
    }

    // 2. READ BY BAND (Retorna DTOs das músicas de uma banda específica)
    @GetMapping("/band/{bandId}")
    public List<SongResponseDTO> getSongsByBand(@PathVariable Long bandId) {
        if (!bandRepository.existsById(bandId)) {
            throw new ResourceNotFoundException("Banda", "id", bandId);
        }
        return songRepository.findByBandId(bandId)
                .stream()
                .map(SongResponseDTO::new)
                .toList();
    }

    // 3. READ ONE (Retorna DTO de uma música por ID)
    @GetMapping("/{id}")
    public ResponseEntity<SongResponseDTO> getSongById(@PathVariable Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));
        return ResponseEntity.ok(new SongResponseDTO(song));
    }

    // 4. CREATE FOR BAND (Cria a música e devolve em formato DTO)
    @PostMapping("/band/{bandId}")
    public ResponseEntity<SongResponseDTO> createSongForBand(@PathVariable Long bandId, @RequestBody Song song) {
        return bandRepository.findById(bandId)
                .map(band -> {
                    song.setBand(band);
                    Song savedSong = songRepository.save(song);
                    return ResponseEntity.ok(new SongResponseDTO(savedSong));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));
    }

    // 5. UPDATE (Edita a música e devolve em formato DTO)
    @PutMapping("/{id}")
    public ResponseEntity<SongResponseDTO> updateSong(@PathVariable Long id, @RequestBody Song songDetails) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        song.setTitle(songDetails.getTitle());
        song.setArtist(songDetails.getArtist());
        song.setSongKey(songDetails.getSongKey());
        song.setDurationSeconds(songDetails.getDurationSeconds());

        Song updatedSong = songRepository.save(song);
        return ResponseEntity.ok(new SongResponseDTO(updatedSong));
    }

    // 6. DELETE - Apagar uma música
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Música", "id", id);
        }
        songRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}