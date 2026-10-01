package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public SongService(SongRepository songRepository, UserRepository userRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public List<SongResponseDTO> getAllSongsForUser(String userEmail) {
        return songRepository.findByUserEmail(userEmail)
                .stream()
                .map(SongResponseDTO::new)
                .toList();
    }

    public SongResponseDTO getSongByIdForUser(Long id, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));
        return new SongResponseDTO(song);
    }

    public SongResponseDTO createSong(SongRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", userEmail));

        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setUser(user);

        Song savedSong = songRepository.save(song);
        return new SongResponseDTO(savedSong);
    }

    public SongResponseDTO updateSong(Long id, SongRequestDTO dto, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());

        Song updatedSong = songRepository.save(song);
        return new SongResponseDTO(updatedSong);
    }

    public void deleteSong(Long id, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));
        songRepository.delete(song);
    }
}