package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final BandRepository bandRepository;
    private final UserRepository userRepository;

    public SongService(SongRepository songRepository, BandRepository bandRepository, UserRepository userRepository) {
        this.songRepository = songRepository;
        this.bandRepository = bandRepository;
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
        song.setDurationSeconds(dto.getDurationSeconds());
        song.setSongKey(dto.getSongKey());
        song.setUser(user);

        if (dto.getBandId() != null) {
            Band band = bandRepository.findByIdAndUserEmail(dto.getBandId(), userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", dto.getBandId()));
            song.setBand(band);
        }

        Song savedSong = songRepository.save(song);
        return new SongResponseDTO(savedSong);
    }

    public SongResponseDTO updateSong(Long id, SongRequestDTO dto, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setDurationSeconds(dto.getDurationSeconds());
        song.setSongKey(dto.getSongKey());

        if (dto.getBandId() != null) {
            Band band = bandRepository.findByIdAndUserEmail(dto.getBandId(), userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", dto.getBandId()));
            song.setBand(band);
        } else {
            song.setBand(null);
        }

        Song updatedSong = songRepository.save(song);
        return new SongResponseDTO(updatedSong);
    }

    public void deleteSong(Long id, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));
        songRepository.delete(song);
    }
}