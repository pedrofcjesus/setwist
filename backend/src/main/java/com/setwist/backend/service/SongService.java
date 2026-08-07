package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final BandRepository bandRepository;

    public SongService(SongRepository songRepository, BandRepository bandRepository) {
        this.songRepository = songRepository;
        this.bandRepository = bandRepository;
    }

    public List<SongResponseDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(SongResponseDTO::new)
                .toList();
    }

    public List<SongResponseDTO> getSongsByBand(Long bandId) {
        if (!bandRepository.existsById(bandId)) {
            throw new ResourceNotFoundException("Banda", "id", bandId);
        }
        return songRepository.findByBandId(bandId)
                .stream()
                .map(SongResponseDTO::new)
                .toList();
    }

    public SongResponseDTO getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));
        return new SongResponseDTO(song);
    }

    public SongResponseDTO createSongForBand(Long bandId, Song song) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));

        song.setBand(band);
        Song savedSong = songRepository.save(song);
        return new SongResponseDTO(savedSong);
    }

    public SongResponseDTO updateSong(Long id, Song songDetails) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        song.setTitle(songDetails.getTitle());
        song.setArtist(songDetails.getArtist());
        song.setSongKey(songDetails.getSongKey());
        song.setDurationSeconds(songDetails.getDurationSeconds());

        Song updatedSong = songRepository.save(song);
        return new SongResponseDTO(updatedSong);
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException("Música", "id", id);
        }
        songRepository.deleteById(id);
    }
}
