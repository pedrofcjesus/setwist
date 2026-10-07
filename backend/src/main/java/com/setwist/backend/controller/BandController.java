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
import com.setwist.backend.dto.BandSuggestionResponseDTO;
import com.setwist.backend.service.BandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bands")
@Tag(name = "Bandas", description = "Gestão de bandas e projetos musicais")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as bandas", description = "Devolve as bandas de que o utilizador autenticado é membro, com a sua permissão em cada uma.")
    @ApiResponse(responseCode = "200", description = "Lista de bandas devolvida com sucesso")
    public List<BandResponseDTO> getAllBands(Principal principal) {
        return bandService.getAllBandsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter banda por ID", description = "Devolve os detalhes de uma banda de que o utilizador é membro.")
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
    @Operation(summary = "Criar nova banda", description = "Cria uma nova banda; o criador fica como administrador e membro.")
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
    @Operation(summary = "Atualizar banda", description = "Atualiza o nome e descrição da banda. Apenas o administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Banda atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
        @ApiResponse(responseCode = "404", description = "Banda não encontrada")
    })
    public ResponseEntity<BandResponseDTO> updateBand(
            @Parameter(description = "ID da banda", required = true) @PathVariable Long id,
            @Valid @RequestBody BandRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandService.updateBand(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar banda", description = "Elimina permanentemente a banda. Apenas o administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Banda eliminada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Sem permissão"),
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
    @Operation(summary = "Listar sugestões da banda", description = "Devolve as músicas sugeridas, com autor e permissões do utilizador atual (canEdit / canRemove).")
    public ResponseEntity<List<BandSuggestionResponseDTO>> getSuggestions(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(bandService.getSuggestionsForBand(id, principal.getName()));
    }

    @PostMapping("/{id}/suggestions/{songId}")
    @Operation(summary = "Adicionar música às sugestões", description = "Qualquer membro sugere uma música do seu próprio catálogo.")
    public ResponseEntity<Void> addSuggestion(
            @PathVariable Long id,
            @PathVariable Long songId,
            Principal principal) {
        bandService.addSuggestionToBand(id, songId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/suggestions/{songId}")
    @Operation(summary = "Remover música das sugestões", description = "O autor retira a sua sugestão; o administrador retira qualquer uma.")
    public ResponseEntity<Void> removeSuggestion(
            @PathVariable Long id,
            @PathVariable Long songId,
            Principal principal) {
        bandService.removeSuggestionFromBand(id, songId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // --- PROMOÇÃO AO REPERTÓRIO ---

    @PostMapping("/{id}/repertoire/{songId}")
    @Operation(summary = "Promover sugestão ao repertório", description = "Apenas o administrador. A música entra no catálogo do administrador (reaproveitando uma igual, ou criando cópia) e sai das sugestões.")
    public ResponseEntity<Void> promoteToRepertoire(
            @PathVariable Long id,
            @PathVariable Long songId,
            Principal principal) {
        bandService.promoteSongToRepertoire(id, songId, principal.getName());
        return ResponseEntity.ok().build();
    }
}