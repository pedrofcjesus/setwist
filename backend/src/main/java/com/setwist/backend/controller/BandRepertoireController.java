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
    @Operation(summary = "Listar repertório", description = "Devolve as músicas do pool da banda. Apenas membros.")
    public ResponseEntity<List<BandRepertoireResponseDTO>> getRepertoire(@PathVariable Long bandId, Principal principal) {
        return ResponseEntity.ok(bandRepertoireService.getRepertoireForBand(bandId, principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Adicionar música ao repertório", description = "Associa uma música do catálogo do administrador à banda. Apenas o administrador.")
    public ResponseEntity<BandRepertoireResponseDTO> addSongToRepertoire(
            @PathVariable Long bandId,
            @Valid @RequestBody BandRepertoireRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandRepertoireService.addSongToBandRepertoire(bandId, dto, principal.getName()));
    }

    @PutMapping("/{repertoireId}")
    @Operation(summary = "Atualizar item do repertório", description = "Atualiza tom, BPM, duração e notas. Apenas o administrador.")
    public ResponseEntity<BandRepertoireResponseDTO> updateRepertoireItem(
            @PathVariable Long bandId,
            @PathVariable Long repertoireId,
            @Valid @RequestBody BandRepertoireUpdateDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandRepertoireService.updateRepertoireItem(bandId, repertoireId, dto, principal.getName()));
    }

    @DeleteMapping("/{repertoireId}")
    @Operation(summary = "Remover música do repertório", description = "Remove um item do repertório da banda. Apenas o administrador.")
    public ResponseEntity<Void> removeSongFromRepertoire(
            @PathVariable Long bandId,
            @PathVariable Long repertoireId,
            Principal principal) {
        bandRepertoireService.removeSongFromRepertoire(bandId, repertoireId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}