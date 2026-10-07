package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.BandSuggestionRepository;
import com.setwist.backend.repository.SetlistSongRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final BandRepertoireRepository bandRepertoireRepository;
    private final SetlistSongRepository setlistSongRepository;
    private final BandSuggestionRepository bandSuggestionRepository;

    public SongService(
            SongRepository songRepository,
            UserRepository userRepository,
            BandRepertoireRepository bandRepertoireRepository,
            SetlistSongRepository setlistSongRepository,
            BandSuggestionRepository bandSuggestionRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.bandRepertoireRepository = bandRepertoireRepository;
        this.setlistSongRepository = setlistSongRepository;
        this.bandSuggestionRepository = bandSuggestionRepository;
    }

    public List<SongResponseDTO> getAllSongsForUser(String userEmail) {
        return songRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Detalhe: inclui também as bandas onde a música está apenas em sugestões (usado no aviso de remoção)
    public SongResponseDTO getSongByIdForUser(Long id, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        List<String> bandNames = bandRepertoireRepository.findBandNamesBySongId(song.getId());
        List<String> suggestedIn = bandSuggestionRepository.findBandNamesBySongId(song.getId());
        return new SongResponseDTO(song, bandNames, suggestedIn);
    }

    public SongResponseDTO createSong(SongRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", userEmail));

        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setUser(user);

        Song savedSong = songRepository.save(song);
        return mapToDTO(savedSong);
    }

    public SongResponseDTO updateSong(Long id, SongRequestDTO dto, String userEmail) {
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());

        Song updatedSong = songRepository.save(song);
        return mapToDTO(updatedSong);
    }

    @Transactional
    public void deleteSong(Long id, String userEmail) {
        // Valida se a música existe e pertence ao utilizador autenticado
        Song song = songRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", id));

        // 1. Apagar primeiro das setlists (dependem do repertório)
        setlistSongRepository.deleteBySongId(song.getId());

        // 2. Apagar do repertório das bandas
        bandRepertoireRepository.deleteBySongId(song.getId());

        // 3. Apagar das sugestões das bandas
        bandSuggestionRepository.deleteBySongId(song.getId());

        // 4. Apagar a música do catálogo
        songRepository.delete(song);
    }

    private SongResponseDTO mapToDTO(Song song) {
        List<String> bandNames = bandRepertoireRepository.findBandNamesBySongId(song.getId());
        return new SongResponseDTO(song, bandNames);
    }
}