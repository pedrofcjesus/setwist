package com.setwist.backend.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongReorderDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.service.SetlistSongService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/setlists/{setlistId}/songs")
@Tag(name = "Músicas da Setlist", description = "Endpoints para adicionar, remover e reordenar músicas dentro de uma setlist")
public class SetlistSongController {

    private final SetlistSongService setlistSongService;

    public SetlistSongController(SetlistSongService setlistSongService) {
        this.setlistSongService = setlistSongService;
    }

    @PostMapping
    @Operation(summary = "Adicionar música à setlist", description = "Associa uma música existente a uma setlist do utilizador autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Música adicionada com sucesso à setlist"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos"),
        @ApiResponse(responseCode = "404", description = "Setlist ou música não encontrada")
    })
    public ResponseEntity<SetlistResponseDTO> addSong(
            @Parameter(description = "ID da setlist", required = true) @PathVariable Long setlistId,
            @Valid @RequestBody SetlistSongRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.addSongToSetlist(setlistId, dto, principal.getName()));
    }

    @DeleteMapping("/{repertoireItemId}")
    @Operation(summary = "Remover música da setlist", description = "Remove a associação de um item de repertório com a setlist.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Música removida da setlist com sucesso"),
        @ApiResponse(responseCode = "404", description = "Setlist ou música não encontrada")
    })
    public ResponseEntity<SetlistResponseDTO> removeSong(
            @Parameter(description = "ID da setlist", required = true) @PathVariable Long setlistId,
            @Parameter(description = "ID do item de repertório a remover", required = true) @PathVariable Long repertoireItemId,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.removeSongFromSetlist(setlistId, repertoireItemId, principal.getName()));
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reordenar músicas da setlist", description = "Atualiza a ordem de apresentação das faixas enviando um array com a nova sequência de IDs de músicas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ordem das músicas atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Lista de IDs inválida ou vazia"),
        @ApiResponse(responseCode = "404", description = "Setlist não encontrada")
    })
    public ResponseEntity<SetlistResponseDTO> reorderSongs(
            @Parameter(description = "ID da setlist", required = true) @PathVariable Long setlistId,
            @Valid @RequestBody SetlistSongReorderDTO dto,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.reorderSongs(setlistId, dto, principal.getName()));
    }
}