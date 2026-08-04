package com.setwist.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;

@RestController
@RequestMapping("/api/setlists")
public class SetlistController {

    private final SetlistRepository setlistRepository;
    private final SongRepository songRepository;
    private final BandRepository bandRepository;

    public SetlistController(SetlistRepository setlistRepository, SongRepository songRepository,
            BandRepository bandRepository) {
        this.setlistRepository = setlistRepository;
        this.songRepository = songRepository;
        this.bandRepository = bandRepository;
    }

    // 1. READ All - Listar todas as Setlists
    @GetMapping
    public List<SetlistResponseDTO> getAllSetlists() {
        return setlistRepository.findAll()
                .stream()
                .map(SetlistResponseDTO::new)
                .toList();
    }

    // 2. READ BY BAND - Mostrar Setlists de uma banda específica
    @GetMapping("/band/{bandId}")
    public List<SetlistResponseDTO> getSetlistsByBand(@PathVariable Long bandId) {
        return setlistRepository.findByBandId(bandId)
                .stream()
                .map(SetlistResponseDTO::new)
                .toList();
    }

    // 3. READ ONE - Mostrar Setlist por ID
    @GetMapping("/{id}")
    public ResponseEntity<SetlistResponseDTO> getSetlistById(@PathVariable Long id) {
        return setlistRepository.findById(id)
                .map(setlist -> ResponseEntity.ok(new SetlistResponseDTO(setlist)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. CREATE - Criar nova setlist
    @PostMapping("/band/{bandId}")
    public ResponseEntity<SetlistResponseDTO> createSetlistForBand(@PathVariable Long bandId,
            @RequestBody Setlist setlist) {
        return bandRepository.findById(bandId)
                .map(band -> {
                    setlist.setBand(band);
                    Setlist savedSetlist = setlistRepository.save(setlist);
                    return ResponseEntity.ok(new SetlistResponseDTO(savedSetlist));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. UPDATE - Editar nome e descrição
    @PutMapping("/{id}")
    public ResponseEntity<SetlistResponseDTO> updateSetlist(@PathVariable Long id,
            @RequestBody Setlist setlistDetails) {
        return setlistRepository.findById(id)
                .map(setlist -> {
                    setlist.setName(setlistDetails.getName());
                    setlist.setDescription(setlistDetails.getDescription());
                    Setlist updatedSetlist = setlistRepository.save(setlist);
                    return ResponseEntity.ok(new SetlistResponseDTO(updatedSetlist));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. DELETE - Apagar uma setlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetlist(@PathVariable Long id) {
        if (setlistRepository.existsById(id)) {
            setlistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GESTÃO DE MÚSICAS

    // 7. ADICIONAR MÚSICA À SETLIST
    @PostMapping("/{setlistId}/songs/{songId}")
    public ResponseEntity<SetlistResponseDTO> addSongToSetlist(@PathVariable Long setlistId,
            @PathVariable Long songId) {
        Setlist setlist = setlistRepository.findById(setlistId).orElse(null);
        Song song = songRepository.findById(songId).orElse(null);

        if (setlist == null || song == null) {
            return ResponseEntity.notFound().build();
        }

        int position = setlist.getSetlistSongs().size() + 1;
        SetlistSong setlistSong = new SetlistSong(setlist, song, position);
        setlist.getSetlistSongs().add(setlistSong);

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return ResponseEntity.ok(new SetlistResponseDTO(updatedSetlist));
    }

    // 8. REMOVER MÚSICA DA SETLIST
    @DeleteMapping("/{setlistId}/songs/{songId}")
    public ResponseEntity<SetlistResponseDTO> removeSongFromSetlist(@PathVariable Long setlistId,
            @PathVariable Long songId) {
        Setlist setlist = setlistRepository.findById(setlistId).orElse(null);

        if (setlist == null) {
            return ResponseEntity.notFound().build();
        }

        boolean removed = setlist.getSetlistSongs().removeIf(item -> item.getSong().getId().equals(songId));

        if (removed) {
            int pos = 1;
            for (SetlistSong item : setlist.getSetlistSongs()) {
                item.setPosition(pos++);
            }
            Setlist updatedSetlist = setlistRepository.save(setlist);
            return ResponseEntity.ok(new SetlistResponseDTO(updatedSetlist));
        }

        return ResponseEntity.ok(new SetlistResponseDTO(setlist));
    }

    // 9. REORDENAR músicas na setlist (para drag&drop)
    @PutMapping("/{setlistId}/reorder")
    public ResponseEntity<SetlistResponseDTO> reorderSetlist(
            @PathVariable Long setlistId,
            @RequestBody List<Long> newSongOrderIds) {

        Setlist setlist = setlistRepository.findById(setlistId).orElse(null);

        if (setlist == null) {
            return ResponseEntity.notFound().build();
        }

        for (SetlistSong setlistSong : setlist.getSetlistSongs()) {
            int newIndex = newSongOrderIds.indexOf(setlistSong.getSong().getId());

            if (newIndex != -1) {
                setlistSong.setPosition(newIndex + 1);
            }
        }

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return ResponseEntity.ok(new SetlistResponseDTO(updatedSetlist));
    }
}