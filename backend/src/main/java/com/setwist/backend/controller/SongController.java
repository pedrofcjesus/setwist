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

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.service.SongService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Músicas", description = "Gestão do repertório global de músicas do utilizador")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as músicas", description = "Devolve o catálogo completo de músicas pertencentes ao utilizador autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de músicas devolvida com sucesso")
    public List<SongResponseDTO> getAllSongs(Principal principal) {
        return songService.getAllSongsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter música por ID", description = "Devolve os detalhes de uma música específica do utilizador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Música encontrada"),
        @ApiResponse(responseCode = "404", description = "Música não encontrada")
    })
    public ResponseEntity<SongResponseDTO> getSongById(
            @Parameter(description = "ID da música", required = true) @PathVariable Long id, 
            Principal principal) {
        return ResponseEntity.ok(songService.getSongByIdForUser(id, principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Criar nova música", description = "Adiciona uma nova música ao repertório do utilizador autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Música criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos")
    })
    public ResponseEntity<SongResponseDTO> createSong(
            @Valid @RequestBody SongRequestDTO dto, 
            Principal principal) {
        return ResponseEntity.ok(songService.createSong(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar música", description = "Atualiza os dados de uma música existente (título, artista, tom, duração, banda).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Música atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos"),
        @ApiResponse(responseCode = "404", description = "Música não encontrada")
    })
    public ResponseEntity<SongResponseDTO> updateSong(
            @Parameter(description = "ID da música", required = true) @PathVariable Long id, 
            @Valid @RequestBody SongRequestDTO dto, 
            Principal principal) {
        return ResponseEntity.ok(songService.updateSong(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar música", description = "Elimina permanentemente uma música do repertório do utilizador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Música eliminada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Música não encontrada")
    })
    public ResponseEntity<Void> deleteSong(
            @Parameter(description = "ID da música a eliminar", required = true) @PathVariable Long id, 
            Principal principal) {
        songService.deleteSong(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}