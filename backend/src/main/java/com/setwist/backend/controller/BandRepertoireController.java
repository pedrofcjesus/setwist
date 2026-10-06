package com.setwist.backend.controller;

import java.security.Principal;
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

import com.setwist.backend.dto.BandRepertoireRequestDTO;
import com.setwist.backend.dto.BandRepertoireResponseDTO;
import com.setwist.backend.dto.BandRepertoireUpdateDTO;
import com.setwist.backend.service.BandRepertoireService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bands/{bandId}/repertoire")
@Tag(name = "Repertório da Banda", description = "Gestão das músicas, tons e BPMs específicos de uma banda")
public class BandRepertoireController {

    private final BandRepertoireService bandRepertoireService;

    public BandRepertoireController(BandRepertoireService bandRepertoireService) {
        this.bandRepertoireService = bandRepertoireService;
    }

    @GetMapping
    @Operation(summary = "Listar repertório", description = "Devolve todas as músicas associadas a esta banda com os respetivos tons e durações.")
    public ResponseEntity<List<BandRepertoireResponseDTO>> getRepertoire(@PathVariable Long bandId) {
        return ResponseEntity.ok(bandRepertoireService.getRepertoireForBand(bandId));
    }

    @PostMapping
    @Operation(summary = "Adicionar música ao repertório", description = "Associa uma música da biblioteca global à banda com definições específicas.")
    public ResponseEntity<BandRepertoireResponseDTO> addSongToRepertoire(
            @PathVariable Long bandId,
            @Valid @RequestBody BandRepertoireRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandRepertoireService.addSongToBandRepertoire(bandId, dto, principal.getName()));
    }

    @PutMapping("/{repertoireId}")
    @Operation(summary = "Atualizar item do repertório", description = "Atualiza as definições de uma música no repertório da banda (tom, BPM, notas).")
    public ResponseEntity<BandRepertoireResponseDTO> updateRepertoireItem(
            @PathVariable Long bandId,
            @PathVariable Long repertoireId,
            @Valid @RequestBody BandRepertoireUpdateDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandRepertoireService.updateRepertoireItem(repertoireId, dto, principal.getName()));
    }

    @DeleteMapping("/{repertoireId}")
    @Operation(summary = "Remover música do repertório", description = "Remove um item do repertório da banda.")
    public ResponseEntity<Void> removeSongFromRepertoire(@PathVariable Long bandId, @PathVariable Long repertoireId) {
        bandRepertoireService.removeSongFromRepertoire(repertoireId);
        return ResponseEntity.noContent().build();
    }
}