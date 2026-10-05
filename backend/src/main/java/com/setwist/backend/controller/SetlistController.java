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

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongReorderDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.service.SetlistService;
import com.setwist.backend.service.SetlistSongService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/setlists")
@Tag(name = "Setlists", description = "Gestão de agrupamentos e alinhamentos de concertos")
public class SetlistController {

    private final SetlistService setlistService;
    private final SetlistSongService setlistSongService;

    public SetlistController(SetlistService setlistService, SetlistSongService setlistSongService) {
        this.setlistService = setlistService;
        this.setlistSongService = setlistSongService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as setlists", description = "Devolve a lista completa de setlists pertencentes ao utilizador autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de setlists devolvida com sucesso")
    public List<SetlistResponseDTO> getAllSetlists(Principal principal) {
        return setlistService.getAllSetlistsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter setlist por ID", description = "Devolve os detalhes de uma setlist específica pertencente ao utilizador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Setlist encontrada"),
        @ApiResponse(responseCode = "404", description = "Setlist não encontrada")
    })
    public ResponseEntity<SetlistResponseDTO> getSetlistById(
            @Parameter(description = "ID da setlist", required = true) @PathVariable Long id, 
            Principal principal) {
        return ResponseEntity.ok(setlistService.getSetlistByIdForUser(id, principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Criar nova setlist", description = "Cria uma nova setlist associada ao utilizador autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Setlist criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos")
    })
    public ResponseEntity<SetlistResponseDTO> createSetlist(
            @Valid @RequestBody SetlistRequestDTO dto, 
            Principal principal) {
        return ResponseEntity.ok(setlistService.createSetlist(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar setlist", description = "Atualiza o nome, descrição ou banda de uma setlist existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Setlist atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos"),
        @ApiResponse(responseCode = "404", description = "Setlist não encontrada")
    })
    public ResponseEntity<SetlistResponseDTO> updateSetlist(
            @Parameter(description = "ID da setlist", required = true) @PathVariable Long id, 
            @Valid @RequestBody SetlistRequestDTO dto, 
            Principal principal) {
        return ResponseEntity.ok(setlistService.updateSetlist(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar setlist", description = "Elimina permanentemente uma setlist do utilizador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Setlist eliminada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Setlist não encontrada")
    })
    public ResponseEntity<Void> deleteSetlist(
            @Parameter(description = "ID da setlist a eliminar", required = true) @PathVariable Long id, 
            Principal principal) {
        setlistService.deleteSetlist(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // --- GESTÃO DE MÚSICAS NA SETLIST ---

    @PostMapping("/{id}/songs")
    @Operation(summary = "Adicionar música à setlist", description = "Adiciona um item do repertório da banda a esta setlist.")
    public ResponseEntity<SetlistResponseDTO> addSongToSetlist(
            @PathVariable Long id,
            @Valid @RequestBody SetlistSongRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.addSongToSetlist(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}/songs/{repertoireItemId}")
    @Operation(summary = "Remover música da setlist", description = "Remove um item do repertório da setlist.")
    public ResponseEntity<SetlistResponseDTO> removeSongFromSetlist(
            @PathVariable Long id,
            @PathVariable Long repertoireItemId,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.removeSongFromSetlist(id, repertoireItemId, principal.getName()));
    }

    @PutMapping("/{id}/songs/reorder")
    @Operation(summary = "Reordenar músicas da setlist", description = "Atualiza a ordem das músicas na setlist.")
    public ResponseEntity<SetlistResponseDTO> reorderSongs(
            @PathVariable Long id,
            @Valid @RequestBody SetlistSongReorderDTO dto,
            Principal principal) {
        return ResponseEntity.ok(setlistSongService.reorderSongs(id, dto, principal.getName()));
    }
}