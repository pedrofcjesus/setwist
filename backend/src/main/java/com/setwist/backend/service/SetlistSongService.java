package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongReorderDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.SetlistRepository;

@Service
public class SetlistSongService {

    private final SetlistRepository setlistRepository;
    private final BandRepertoireRepository bandRepertoireRepository;

    public SetlistSongService(SetlistRepository setlistRepository, BandRepertoireRepository bandRepertoireRepository) {
        this.setlistRepository = setlistRepository;
        this.bandRepertoireRepository = bandRepertoireRepository;
    }

    @Transactional
    public SetlistResponseDTO addSongToSetlist(Long setlistId, SetlistSongRequestDTO dto, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(setlistId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        BandRepertoire repertoireItem = bandRepertoireRepository.findById(dto.getRepertoireItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", dto.getRepertoireItemId()));

        // Evita adicionar o mesmo item de repertório em duplicado na mesma setlist
        boolean exists = setlist.getSetlistSongs().stream()
                .anyMatch(ss -> ss.getRepertoireItem() != null && ss.getRepertoireItem().getId().equals(repertoireItem.getId()));

        if (!exists) {
            int position = (dto.getPosition() != null) ? dto.getPosition() : setlist.getSetlistSongs().size() + 1;
            SetlistSong setlistSong = new SetlistSong(setlist, repertoireItem, position);
            if (dto.getNotes() != null) {
                setlistSong.setNotes(dto.getNotes());
            }
            setlist.getSetlistSongs().add(setlistSong);
            setlistRepository.save(setlist);
        }

        return new SetlistResponseDTO(setlistRepository.findByIdAndUserEmail(setlistId, userEmail).get());
    }

    @Transactional
    public SetlistResponseDTO removeSongFromSetlist(Long setlistId, Long repertoireItemId, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(setlistId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        setlist.getSetlistSongs().removeIf(ss -> ss.getRepertoireItem() != null && ss.getRepertoireItem().getId().equals(repertoireItemId));

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

        List<Long> newOrderIds = dto.getSongIds(); // IDs dos itens de repertório na nova ordem

        for (SetlistSong ss : setlist.getSetlistSongs()) {
            if (ss.getRepertoireItem() != null) {
                int newPos = newOrderIds.indexOf(ss.getRepertoireItem().getId());
                if (newPos != -1) {
                    ss.setPosition(newPos + 1);
                }
            }
        }

        setlist.getSetlistSongs().sort((a, b) -> Integer.compare(a.getPosition(), b.getPosition()));
        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }
}
