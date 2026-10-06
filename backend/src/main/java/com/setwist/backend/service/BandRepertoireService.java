package com.setwist.backend.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.BandRepertoireRequestDTO;
import com.setwist.backend.dto.BandRepertoireResponseDTO;
import com.setwist.backend.dto.BandRepertoireUpdateDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistSongRepository;
import com.setwist.backend.repository.SongRepository;

@Service
public class BandRepertoireService {

    private final BandRepertoireRepository bandRepertoireRepository;
    private final BandRepository bandRepository;
    private final SongRepository songRepository;
    private final SetlistSongRepository setlistSongRepository;

    public BandRepertoireService(
            BandRepertoireRepository bandRepertoireRepository,
            BandRepository bandRepository,
            SongRepository songRepository,
            SetlistSongRepository setlistSongRepository) {
        this.bandRepertoireRepository = bandRepertoireRepository;
        this.bandRepository = bandRepository;
        this.songRepository = songRepository;
        this.setlistSongRepository = setlistSongRepository;
    }

    public List<BandRepertoireResponseDTO> getRepertoireForBand(Long bandId) {
        return bandRepertoireRepository.findByBandId(bandId)
                .stream()
                .map(BandRepertoireResponseDTO::new)
                .toList();
    }

    public BandRepertoireResponseDTO addSongToBandRepertoire(Long bandId, BandRepertoireRequestDTO dto, String userEmail) {
        Band band = bandRepository.findByIdAndUserEmail(bandId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));

        Song song = songRepository.findByIdAndUserEmail(dto.getSongId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", dto.getSongId()));

        BandRepertoire repertoire = new BandRepertoire(band, song, dto.getSongKey(), dto.getDurationSeconds(), dto.getBpm());
        repertoire.setNotes(dto.getNotes());

        BandRepertoire saved = bandRepertoireRepository.save(repertoire);
        return new BandRepertoireResponseDTO(saved);
    }

    @Transactional
    public void removeSongFromRepertoire(Long repertoireId) {
        BandRepertoire repertoire = bandRepertoireRepository.findById(repertoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireId));

        // 1. Remove primeiro a música de quaisquer setlists onde esteja associada
        setlistSongRepository.deleteByRepertoireItemId(repertoireId);

        // 2. Remove o item do repertório principal da banda
        bandRepertoireRepository.delete(repertoire);
    }

    @Transactional
    public BandRepertoireResponseDTO updateRepertoireItem(Long repertoireId, BandRepertoireUpdateDTO dto, String userEmail) {
        BandRepertoire repertoire = bandRepertoireRepository.findById(repertoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireId));

        // Validação de Segurança: verifica se o item pertence à banda do utilizador autenticado
        if (!repertoire.getBand().getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Não tem permissão para alterar este item de repertório.");
        }

        if (dto.getSongKey() != null) {
            repertoire.setSongKey(dto.getSongKey());
        }
        if (dto.getBpm() != null) {
            repertoire.setBpm(dto.getBpm());
        }
        if (dto.getDurationSeconds() != null) {
            repertoire.setDurationSeconds(dto.getDurationSeconds());
        }
        if (dto.getNotes() != null) {
            repertoire.setNotes(dto.getNotes());
        }

        BandRepertoire updated = bandRepertoireRepository.save(repertoire);
        return new BandRepertoireResponseDTO(updated);
    }
}