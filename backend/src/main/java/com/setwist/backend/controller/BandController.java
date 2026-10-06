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

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.service.BandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bands")
@Tag(name = "Bandas", description = "Gestão de bandas e projetos musicais do utilizador")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as bandas", description = "Devolve a lista de bandas associadas ao utilizador autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de bandas devolvida com sucesso")
    public List<BandResponseDTO> getAllBands(Principal principal) {
        return bandService.getAllBandsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter banda por ID", description = "Devolve os detalhes de uma banda específica pertencente ao utilizador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banda encontrada"),
        @ApiResponse(responseCode = "404", description = "Banda não encontrada")
    })
    public ResponseEntity<BandResponseDTO> getBandById(
            @Parameter(description = "ID da banda", required = true) @PathVariable Long id, 
            Principal principal) {
        return ResponseEntity.ok(bandService.getBandByIdForUser(id, principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Criar nova banda", description = "Cria uma nova banda associada ao utilizador autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banda criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos")
    })
    public ResponseEntity<BandResponseDTO> createBand(
            @Valid @RequestBody BandRequestDTO dto, 
            Principal principal) {
        return ResponseEntity.ok(bandService.createBand(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar banda", description = "Atualiza o nome e descrição de uma banda existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banda atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos"),
        @ApiResponse(responseCode = "404", description = "Banda não encontrada")
    })
    public ResponseEntity<BandResponseDTO> updateBand(
            @Parameter(description = "ID da banda", required = true) @PathVariable Long id, 
            @Valid @RequestBody BandRequestDTO dto, 
            Principal principal) {
        return ResponseEntity.ok(bandService.updateBand(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar banda", description = "Elimina permanentemente uma banda do utilizador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Banda eliminada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Banda não encontrada")
    })
    public ResponseEntity<Void> deleteBand(
            @Parameter(description = "ID da banda a eliminar", required = true) @PathVariable Long id, 
            Principal principal) {
        bandService.deleteBand(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINTS DE SUGESTÕES ---

    @GetMapping("/{id}/suggestions")
    @Operation(summary = "Listar sugestões da banda", description = "Devolve as músicas sugeridas para a banda.")
    public ResponseEntity<List<SongResponseDTO>> getSuggestions(
            @PathVariable Long id, 
            Principal principal) {
        return ResponseEntity.ok(bandService.getSuggestionsForBand(id, principal.getName()));
    }

    @PostMapping("/{id}/suggestions/{songId}")
    @Operation(summary = "Adicionar música às sugestões", description = "Associa uma música do catálogo às sugestões da banda.")
    public ResponseEntity<Void> addSuggestion(
            @PathVariable Long id, 
            @PathVariable Long songId, 
            Principal principal) {
        bandService.addSuggestionToBand(id, songId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/suggestions/{songId}")
    @Operation(summary = "Remover música das sugestões", description = "Remove a música das sugestões da banda sem a apagar do catálogo global.")
    public ResponseEntity<Void> removeSuggestion(
            @PathVariable Long id, 
            @PathVariable Long songId, 
            Principal principal) {
        bandService.removeSuggestionFromBand(id, songId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // --- PROMOÇÃO AO REPERTÓRIO ---

    @PostMapping("/{id}/repertoire/{songId}")
    @Operation(summary = "Adicionar/Promover música ao repertório", description = "Adiciona a música ao repertório da banda e remove-a automaticamente das sugestões.")
    public ResponseEntity<Void> promoteToRepertoire(
            @PathVariable Long id, 
            @PathVariable Long songId, 
            Principal principal) {
        bandService.promoteSongToRepertoire(id, songId, principal.getName());
        return ResponseEntity.ok().build();
    }
}