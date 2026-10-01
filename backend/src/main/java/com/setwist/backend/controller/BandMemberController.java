package com.setwist.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setwist.backend.dto.BandMemberRequestDTO;
import com.setwist.backend.dto.BandMemberResponseDTO;
import com.setwist.backend.service.BandMemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bands/{bandId}/members")
@Tag(name = "Membros da Banda", description = "Gestão de músicos e permissões dentro de uma banda")
public class BandMemberController {

    private final BandMemberService bandMemberService;

    public BandMemberController(BandMemberService bandMemberService) {
        this.bandMemberService = bandMemberService;
    }

    @GetMapping
    @Operation(summary = "Listar membros", description = "Devolve todos os membros associados a esta banda.")
    public ResponseEntity<List<BandMemberResponseDTO>> getMembers(@PathVariable Long bandId) {
        return ResponseEntity.ok(bandMemberService.getMembersByBand(bandId));
    }

    @PostMapping
    @Operation(summary = "Adicionar membro", description = "Adiciona um utilizador existente à banda com uma permissão específica.")
    public ResponseEntity<BandMemberResponseDTO> addMember(
            @PathVariable Long bandId,
            @Valid @RequestBody BandMemberRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandMemberService.addMemberToBand(bandId, dto, principal.getName()));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Remover membro", description = "Remove um utilizador da banda.")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long bandId,
            @PathVariable Long userId,
            Principal principal) {
        bandMemberService.removeMemberFromBand(bandId, userId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}