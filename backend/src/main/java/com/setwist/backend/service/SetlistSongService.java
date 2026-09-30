package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongReorderDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;

@Service
public class SetlistSongService {

    private final SetlistRepository setlistRepository;
    private final SongRepository songRepository;

    public SetlistSongService(SetlistRepository setlistRepository, SongRepository songRepository) {
        this.setlistRepository = setlistRepository;
        this.songRepository = songRepository;
    }

    @Transactional
    public SetlistResponseDTO addSongToSetlist(Long setlistId, SetlistSongRequestDTO dto, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(setlistId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        Song song = songRepository.findByIdAndUserEmail(dto.getSongId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Música", "id", dto.getSongId()));

        // Evita adicionar a mesma música em duplicado na mesma setlist
        boolean exists = setlist.getSetlistSongs().stream()
                .anyMatch(ss -> ss.getSong().getId().equals(song.getId()));

        if (!exists) {
            int position = (dto.getPosition() != null) ? dto.getPosition() : setlist.getSetlistSongs().size() + 1;
            SetlistSong setlistSong = new SetlistSong(setlist, song, position);
            setlist.getSetlistSongs().add(setlistSong);
            setlistRepository.save(setlist);
        }

        return new SetlistResponseDTO(setlistRepository.findByIdAndUserEmail(setlistId, userEmail).get());
    }

    @Transactional
    public SetlistResponseDTO removeSongFromSetlist(Long setlistId, Long songId, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(setlistId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        setlist.getSetlistSongs().removeIf(ss -> ss.getSong().getId().equals(songId));

        // Reajusta as posições das músicas restantes
        int pos = 1;
        for (SetlistSong ss : setlist.getSetlistSongs()) {
            ss.setPosition(pos++);
        }

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }

    @Transactional
    public SetlistResponseDTO reorderSongs(Long setlistId, SetlistSongReorderDTO dto, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(setlistId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        List<Long> newOrderIds = dto.getSongIds();

        for (SetlistSong ss : setlist.getSetlistSongs()) {
            int newPos = newOrderIds.indexOf(ss.getSong().getId());
            if (newPos != -1) {
                ss.setPosition(newPos + 1);
            }
        }

        setlist.getSetlistSongs().sort((a, b) -> Integer.compare(a.getPosition(), b.getPosition()));
        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }
}
