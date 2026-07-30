package com.setwist.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    // Endpoint para LER (Listar todas as músicas)
    @GetMapping
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    // Endpoint para Criar (Adicionar músicas)
    @PostMapping
    public Song createSong(@RequestBody Song song) {
        return songRepository.save(song);
    }
}
