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

import com.setwist.backend.dto.BandMemberRequestDTO;
import com.setwist.backend.dto.BandMemberResponseDTO;
import com.setwist.backend.dto.BandMemberUpdateDTO;
import com.setwist.backend.service.BandMemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bands/{bandId}/members")
@Tag(name = "Membros da Banda", description = "Gestão de músicos e funções dentro de uma banda")
public class BandMemberController {

    private final BandMemberService bandMemberService;

    public BandMemberController(BandMemberService bandMemberService) {
        this.bandMemberService = bandMemberService;
    }

    @GetMapping
    @Operation(summary = "Listar membros", description = "Devolve os membros da banda (administrador primeiro), com permissão e função. Apenas membros.")
    public ResponseEntity<List<BandMemberResponseDTO>> getMembers(@PathVariable Long bandId, Principal principal) {
        return ResponseEntity.ok(bandMemberService.getMembersByBand(bandId, principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Adicionar membro", description = "Adiciona um utilizador existente à banda como membro, com uma função opcional. Apenas o administrador.")
    public ResponseEntity<BandMemberResponseDTO> addMember(
            @PathVariable Long bandId,
            @Valid @RequestBody BandMemberRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandMemberService.addMemberToBand(bandId, dto, principal.getName()));
    }

    // Declarado antes de "/{userId}" por clareza; o Spring dá prioridade ao caminho literal
    @DeleteMapping("/me")
    @Operation(summary = "Sair da banda", description = "O membro sai da banda. O administrador não pode sair (só apagar a banda).")
    public ResponseEntity<Void> leaveBand(@PathVariable Long bandId, Principal principal) {
        bandMemberService.leaveBand(bandId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Atualizar função do membro", description = "Define a função (texto livre) de um membro, incluindo o próprio administrador. Apenas o administrador.")
    public ResponseEntity<BandMemberResponseDTO> updateMember(
            @PathVariable Long bandId,
            @PathVariable Long userId,
            @Valid @RequestBody BandMemberUpdateDTO dto,
            Principal principal) {
        return ResponseEntity.ok(bandMemberService.updateMemberInstrument(bandId, userId, dto, principal.getName()));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Remover membro", description = "Remove um membro da banda. Apenas o administrador; o administrador não pode ser removido.")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long bandId,
            @PathVariable Long userId,
            Principal principal) {
        bandMemberService.removeMemberFromBand(bandId, userId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}