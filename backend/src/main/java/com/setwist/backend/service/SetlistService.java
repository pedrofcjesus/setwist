package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;

@Service
public class SetlistService {

    private final SetlistRepository setlistRepository;
    private final SongRepository songRepository;
    private final BandRepository bandRepository;

    public SetlistService(SetlistRepository setlistRepository, SongRepository songRepository,
            BandRepository bandRepository) {
        this.setlistRepository = setlistRepository;
        this.songRepository = songRepository;
        this.bandRepository = bandRepository;
    }

    public List<SetlistResponseDTO> getAllSetlists() {
        return setlistRepository.findAll()
                .stream()
                .map(SetlistResponseDTO::new)
                .toList();
    }

    public List<SetlistResponseDTO> getSetlistsByBand(Long bandId) {
        if (!bandRepository.existsById(bandId)) {
            throw new ResourceNotFoundException("Banda", "id", bandId);
        }
        return setlistRepository.findByBandId(bandId)
                .stream()
                .map(SetlistResponseDTO::new)
                .toList();
    }

    public SetlistResponseDTO getSetlistById(Long id) {
        Setlist setlist = setlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", id));
        return new SetlistResponseDTO(setlist);
    }

    public SetlistResponseDTO createSetlistForBand(Long bandId, SetlistRequestDTO dto) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));

        Setlist setlist = new Setlist();
        setlist.setName(dto.getName());
        setlist.setDescription(dto.getDescription());
        setlist.setBand(band);

        Setlist savedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(savedSetlist);
    }

    public SetlistResponseDTO updateSetlist(Long id, SetlistRequestDTO dto) {
        Setlist setlist = setlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", id));

        setlist.setName(dto.getName());
        setlist.setDescription(dto.getDescription());

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }

    public void deleteSetlist(Long id) {
        if (!setlistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Setlist", "id", id);
        }
        setlistRepository.deleteById(id);
    }

    public SetlistResponseDTO addSongToSetlist(Long setlistId, Long songId) {
        Setlist setlist = setlistRepository.findById(setlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", songId));

        int position = setlist.getSetlistSongs().size() + 1;
        SetlistSong setlistSong = new SetlistSong(setlist, song, position);
        setlist.getSetlistSongs().add(setlistSong);

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }

    public SetlistResponseDTO removeSongFromSetlist(Long setlistId, Long songId) {
        Setlist setlist = setlistRepository.findById(setlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        boolean removed = setlist.getSetlistSongs().removeIf(item -> item.getSong().getId().equals(songId));

        if (removed) {
            int pos = 1;
            for (SetlistSong item : setlist.getSetlistSongs()) {
                item.setPosition(pos++);
            }
            Setlist updatedSetlist = setlistRepository.save(setlist);
            return new SetlistResponseDTO(updatedSetlist);
        }

        return new SetlistResponseDTO(setlist);
    }

    public SetlistResponseDTO reorderSetlist(Long setlistId, List<Long> newSongOrderIds) {
        Setlist setlist = setlistRepository.findById(setlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        for (SetlistSong setlistSong : setlist.getSetlistSongs()) {
            int newIndex = newSongOrderIds.indexOf(setlistSong.getSong().getId());

            if (newIndex != -1) {
                setlistSong.setPosition(newIndex + 1);
            }
        }

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }
}