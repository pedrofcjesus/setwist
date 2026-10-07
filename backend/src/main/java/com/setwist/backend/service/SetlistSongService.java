package com.setwist.backend.service;

import java.util.Comparator;
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
    private final BandAccessService accessService;

    public SetlistSongService(
            SetlistRepository setlistRepository,
            BandRepertoireRepository bandRepertoireRepository,
            BandAccessService accessService) {
        this.setlistRepository = setlistRepository;
        this.bandRepertoireRepository = bandRepertoireRepository;
        this.accessService = accessService;
    }

    @Transactional
    public SetlistResponseDTO addSongToSetlist(Long setlistId, SetlistSongRequestDTO dto, String userEmail) {
        Setlist setlist = accessService.requireSetlistEdit(setlistId, userEmail);

        BandRepertoire repertoireItem = resolveRepertoireItem(setlist, dto.getRepertoireItemId(), userEmail);

        // Evita adicionar o mesmo item de repertório em duplicado na mesma setlist
        boolean exists = setlist.getSetlistSongs().stream()
                .anyMatch(ss -> ss.getRepertoireItem() != null && ss.getRepertoireItem().getId().equals(repertoireItem.getId()));

        if (exists) {
            return toDTO(setlist);
        }

        int position = (dto.getPosition() != null) ? dto.getPosition() : setlist.getSetlistSongs().size() + 1;
        SetlistSong setlistSong = new SetlistSong(setlist, repertoireItem, position);
        if (dto.getNotes() != null) {
            setlistSong.setNotes(dto.getNotes());
        }
        setlist.getSetlistSongs().add(setlistSong);

        return toDTO(setlistRepository.saveAndFlush(setlist));
    }

    @Transactional
    public SetlistResponseDTO removeSongFromSetlist(Long setlistId, Long repertoireItemId, String userEmail) {
        Setlist setlist = accessService.requireSetlistEdit(setlistId, userEmail);

        setlist.getSetlistSongs().removeIf(ss -> ss.getRepertoireItem() != null && ss.getRepertoireItem().getId().equals(repertoireItemId));

        // Reajusta as posições das músicas restantes
        int pos = 1;
        for (SetlistSong ss : setlist.getSetlistSongs()) {
            ss.setPosition(pos++);
        }

        return toDTO(setlistRepository.save(setlist));
    }

    @Transactional
    public SetlistResponseDTO reorderSongs(Long setlistId, SetlistSongReorderDTO dto, String userEmail) {
        Setlist setlist = accessService.requireSetlistEdit(setlistId, userEmail);

        List<Long> newOrderIds = dto.getSongIds(); // IDs dos itens de repertório na nova ordem

        if (newOrderIds != null && setlist.getSetlistSongs() != null) {
            for (SetlistSong ss : setlist.getSetlistSongs()) {
                if (ss.getRepertoireItem() != null && ss.getRepertoireItem().getId() != null) {
                    int newPos = newOrderIds.indexOf(ss.getRepertoireItem().getId());
                    if (newPos != -1) {
                        ss.setPosition(newPos + 1);
                    }
                }
            }

            // Ordenação segura contra nulos e compatível com o Hibernate
            setlist.getSetlistSongs().sort(
                Comparator.comparing(
                    ss -> ss.getPosition(),
                    Comparator.nullsLast(Comparator.naturalOrder())
                )
            );
        }

        return toDTO(setlistRepository.save(setlist));
    }

    // O item tem de pertencer ao repertório da banda da setlist
    private BandRepertoire resolveRepertoireItem(Setlist setlist, Long repertoireItemId, String userEmail) {
        if (setlist.getBand() != null) {
            return bandRepertoireRepository.findByIdAndBandId(repertoireItemId, setlist.getBand().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireItemId));
        }

        // Setlist pessoal (sem banda): o item tem de ser de uma banda de que o utilizador é membro
        BandRepertoire item = bandRepertoireRepository.findById(repertoireItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Repertório", "id", repertoireItemId));
        accessService.requireMembership(item.getBand().getId(), userEmail);
        return item;
    }

    private SetlistResponseDTO toDTO(Setlist setlist) {
        SetlistResponseDTO dto = new SetlistResponseDTO(setlist);
        dto.setCanEdit(true); // quem chega aqui já passou por requireSetlistEdit
        return dto;
    }
}