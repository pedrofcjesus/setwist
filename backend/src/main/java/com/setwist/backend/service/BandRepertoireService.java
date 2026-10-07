package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.BandRepertoireRequestDTO;
import com.setwist.backend.dto.BandRepertoireResponseDTO;
import com.setwist.backend.dto.BandRepertoireUpdateDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.SetlistSongRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.util.SongMatcher;

@Service
public class BandRepertoireService {

    private final BandRepertoireRepository bandRepertoireRepository;
    private final SongRepository songRepository;
    private final SetlistSongRepository setlistSongRepository;
    private final BandAccessService accessService;

    public BandRepertoireService(
            BandRepertoireRepository bandRepertoireRepository,
            SongRepository songRepository,
            SetlistSongRepository setlistSongRepository,
            BandAccessService accessService) {
        this.bandRepertoireRepository = bandRepertoireRepository;
        this.songRepository = songRepository;
        this.setlistSongRepository = setlistSongRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<BandRepertoireResponseDTO> getRepertoireForBand(Long bandId, String userEmail) {
        accessService.requireMembership(bandId, userEmail);

        return bandRepertoireRepository.findByBandId(bandId)
                .stream()
                .map(BandRepertoireResponseDTO::new)
                .toList();
    }

    @Transactional
    public BandRepertoireResponseDTO addSongToBandRepertoire(Long bandId, BandRepertoireRequestDTO dto, String userEmail) {
        BandMember admin = accessService.requireAdmin(bandId, userEmail);
        Band band = admin.getBand();

        Song song = songRepository.findByIdAndUserEmail(dto.getSongId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", dto.getSongId()));

        BandRepertoire repertoire = new BandRepertoire(band, song, dto.getSongKey(), dto.getDurationSeconds(), dto.getBpm());
        repertoire.setNotes(dto.getNotes());

        BandRepertoire saved = bandRepertoireRepository.save(repertoire);

        // Se a música estava nas sugestões da banda, sai das sugestões
        band.getSuggestions().removeIf(s -> SongMatcher.isSameSong(s.getSong(), song));

        return new BandRepertoireResponseDTO(saved);
    }

    @Transactional
    public void removeSongFromRepertoire(Long bandId, Long repertoireId, String userEmail) {
        accessService.requireAdmin(bandId, userEmail);

        BandRepertoire repertoire = bandRepertoireRepository.findByIdAndBandId(repertoireId, bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireId));

        // 1. Remove primeiro a música de quaisquer setlists onde esteja associada
        setlistSongRepository.deleteByRepertoireItemId(repertoireId);

        // 2. Remove o item do repertório principal da banda
        bandRepertoireRepository.delete(repertoire);
    }

    @Transactional
    public BandRepertoireResponseDTO updateRepertoireItem(Long bandId, Long repertoireId, BandRepertoireUpdateDTO dto, String userEmail) {
        accessService.requireAdmin(bandId, userEmail);

        BandRepertoire repertoire = bandRepertoireRepository.findByIdAndBandId(repertoireId, bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireId));

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