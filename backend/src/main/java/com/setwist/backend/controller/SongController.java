package com.setwist.backend.controller;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.model.Song;
import com.setwist.backend.repository.SongRepository;

@RestController
@RequestMapping("/api/songs")

public class SongController {

    // Variável que guarda o repository
    private final SongRepository songRepository;

    // Construtor
    public SongController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    // 1. READ ALL (Listar todas as músicas)
    @GetMapping
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    // 2. READ ONE (Listar uma música por ID)
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return songRepository.findById(id)
                .map(song -> ResponseEntity.ok(song))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREATE (Adicionar músicas)
    @PostMapping
    public Song createSong(@RequestBody Song song) {
        return songRepository.save(song);
    }

    // 4. UPDATE (Editar música existente)
    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable Long id, @RequestBody Song songDetails) {
        return songRepository.findById(id)
                .map(song -> {
                    song.setTitle(songDetails.getTitle());
                    song.setArtist(songDetails.getArtist());
                    song.setSongKey(songDetails.getSongKey());
                    song.setDurationSeconds(songDetails.getDurationSeconds());
                    Song updatedSong = songRepository.save(song);
                    return ResponseEntity.ok(updatedSong);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. DELETE - Apagar uma música
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
