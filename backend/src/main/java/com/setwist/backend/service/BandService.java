package com.setwist.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class BandService {

    private final BandRepository bandRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public BandService(BandRepository bandRepository, SongRepository songRepository, UserRepository userRepository) {
        this.bandRepository = bandRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    // --- CRUD DE BANDAS ---

    @Transactional(readOnly = true)
    public List<BandResponseDTO> getAllBandsForUser(String userEmail) {
        return bandRepository.findByUserEmail(userEmail).stream()
                .map(this::mapToBandDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BandResponseDTO getBandByIdForUser(Long bandId, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        return mapToBandDTO(band);
    }

    @Transactional
    public BandResponseDTO createBand(BandRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Band band = new Band(dto.getName(), dto.getDescription());
        band.setUser(user);

        Band savedBand = bandRepository.save(band);
        return mapToBandDTO(savedBand);
    }

    @Transactional
    public BandResponseDTO updateBand(Long bandId, BandRequestDTO dto, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());

        Band updatedBand = bandRepository.save(band);
        return mapToBandDTO(updatedBand);
    }

    @Transactional
    public void deleteBand(Long bandId, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        bandRepository.delete(band);
    }

    // --- GESTÃO DE SUGESTÕES ---

    @Transactional(readOnly = true)
    public List<SongResponseDTO> getSuggestionsForBand(Long bandId, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        return band.getSuggestions().stream()
                .map(this::mapToSongDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addSuggestionToBand(Long bandId, Long songId, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        Song song = songRepository.findByIdAndUserEmail(songId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));

        if (!band.getSuggestions().contains(song)) {
            band.getSuggestions().add(song);
            bandRepository.save(band);
        }
    }

    @Transactional
    public void removeSuggestionFromBand(Long bandId, Long songId, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        band.getSuggestions().removeIf(song -> song.getId().equals(songId));
        bandRepository.save(band);
    }

    @Transactional
    public void promoteSongToRepertoire(Long bandId, Long songId, String userEmail) {
        Band band = getBandByIdAndUser(bandId, userEmail);
        Song song = songRepository.findByIdAndUserEmail(songId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));

        // 1. Adiciona ao repertório se ainda não existir
        boolean alreadyInRepertoire = band.getRepertoire().stream()
                .anyMatch(r -> r.getSong().getId().equals(songId));

        if (!alreadyInRepertoire) {
            BandRepertoire newRepertoireEntry = new BandRepertoire();
            newRepertoireEntry.setBand(band);
            newRepertoireEntry.setSong(song);
            band.getRepertoire().add(newRepertoireEntry);
        }

        // 2. Remove automaticamente das sugestões
        band.getSuggestions().removeIf(s -> s.getId().equals(songId));

        bandRepository.save(band);
    }

    // --- MÉTODOS AUXILIARES E MAPEAMENTOS ---

    private Band getBandByIdAndUser(Long bandId, String userEmail) {
        return bandRepository.findByIdAndUserEmail(bandId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Band", "id", bandId));
    }

    private BandResponseDTO mapToBandDTO(Band band) {
        BandResponseDTO dto = new BandResponseDTO();
        dto.setId(band.getId());
        dto.setName(band.getName());
        dto.setDescription(band.getDescription());
        return dto;
    }

    private SongResponseDTO mapToSongDTO(Song song) {
        SongResponseDTO dto = new SongResponseDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setArtist(song.getArtist());
        return dto;
    }
}