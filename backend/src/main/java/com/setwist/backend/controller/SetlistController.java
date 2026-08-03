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

import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;

@RestController
@RequestMapping("/api/setlists")
public class SetlistController {

    private final SongController songController;
    private final SetlistRepository setlistRepository;
    private final SongRepository songRepository;

    public SetlistController(SetlistRepository setlistRepository, SongRepository songRepository,
            SongController songController) {
        this.setlistRepository = setlistRepository;
        this.songRepository = songRepository;
        this.songController = songController;
    }

    // 1. READ All - Listar todas as Setlists
    @GetMapping
    public List<Setlist> getAllSetlists() {
        return setlistRepository.findAll();
    }

    // 2. READ ONE - Mostrar Setlist por ID
    @GetMapping("/{id}")
    public ResponseEntity<Setlist> getSetlistById(@PathVariable Long id) {
        return setlistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREATE - Criar nova setlist
    @PostMapping
    public Setlist createSetlist(@RequestBody Setlist setlist) {
        return setlistRepository.save(setlist);
    }

    // 4. UPDATE - Editar nome e descrição
    @PutMapping("/{id}")
    public ResponseEntity<Setlist> updateSetlist(@PathVariable Long id, @RequestBody Setlist setlistDetails) {
        return setlistRepository.findById(id)
                .map(setlist -> {
                    setlist.setName(setlistDetails.getName());
                    setlist.setDescription(setlistDetails.getDescription());
                    Setlist updatedSetlist = setlistRepository.save(setlist);
                    return ResponseEntity.ok(updatedSetlist);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. DELETE - Apagar uma setlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetlist(@PathVariable Long id) {
        if (setlistRepository.existsById(id)) {
            setlistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GESTÂO DE MÚSICAS

    // 6. ADICIONAR MÚSICA À SETLIST (com posição automatica no fim do alinhamento)
    // POST /api/setlist/1/songs/2
    @PostMapping("/{setlistId}/songs/{songId}")
    public ResponseEntity<Setlist> addSongToSetlist(@PathVariable Long setlistId, @PathVariable Long songId) {
        Setlist setlist = setlistRepository.findById(setlistId).orElse(null);
        Song song = songRepository.findById(songId).orElse(null);

        if (setlist == null || song == null) {
            return ResponseEntity.notFound().build();
        }

        // posição = número atual + 1
        int nextPosition = setlist.getSetlistSongs().size() + 1;

        SetlistSong setlistSong = new SetlistSong(setlist, song, nextPosition);
        setlist.getSetlistSongs().add(setlistSong);

        setlistRepository.save(setlist);

        return ResponseEntity.ok(setlist);
    }

    // .7 REMOVER MÚSICA DA SETLIST
    @DeleteMapping("/{setlistId}/songs/{songId}")
    public ResponseEntity<Setlist> removeSongFromSetlist(@PathVariable Long setlistId, @PathVariable Long songId) {
        Setlist setlist = setlistRepository.findById(setlistId).orElse(null);

        if (setlist == null) {
            return ResponseEntity.notFound().build();
        }

        // Remove o elemento da lista
        boolean removed = setlist.getSetlistSongs().removeIf(item -> item.getSong().getId().equals(songId));

        if (removed) {
            // Reajusta as posições das músicas restantes
            int pos = 1;
            for (SetlistSong item : setlist.getSetlistSongs()) {
                item.setPosition(pos++);
            }
            setlistRepository.save(setlist);
        }

        return ResponseEntity.ok(setlist);
    }

    // 8. REORDENAR músicas na setlist (para drag&drop)
    @PutMapping("/{setlistId}/reorder")
    public ResponseEntity<Setlist> reorderSetlist(
            @PathVariable Long setlistId,
            @RequestBody List<Long> newSongOrderIds) {

        Setlist setlist = setlistRepository.findById(setlistId).orElse(null);

        if (setlist == null) {
            return ResponseEntity.notFound().build();
        }

        for (com.setwist.backend.model.SetlistSong setlistSong : setlist.getSetlistSongs()) {
            int newIndex = newSongOrderIds.indexOf(setlistSong.getSong().getId());

            if (newIndex != -1) {
                setlistSong.setPosition(newIndex + 1);
            }

        }
        setlistRepository.save(setlist);

        return ResponseEntity.ok(setlist);
    }

}
