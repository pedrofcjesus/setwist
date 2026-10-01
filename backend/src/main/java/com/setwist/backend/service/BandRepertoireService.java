package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.BandRepertoireRequestDTO;
import com.setwist.backend.dto.BandRepertoireResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;

@Service
public class BandRepertoireService {

    private final BandRepertoireRepository bandRepertoireRepository;
    private final BandRepository bandRepository;
    private final SongRepository songRepository;

    public BandRepertoireService(
            BandRepertoireRepository bandRepertoireRepository,
            BandRepository bandRepository,
            SongRepository songRepository) {
        this.bandRepertoireRepository = bandRepertoireRepository;
        this.bandRepository = bandRepository;
        this.songRepository = songRepository;
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

    public void removeSongFromRepertoire(Long repertoireId) {
        BandRepertoire repertoire = bandRepertoireRepository.findById(repertoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireId));
        bandRepertoireRepository.delete(repertoire);
    }
}