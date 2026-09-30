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
import com.setwist.backend.service.BandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping
    public List<BandResponseDTO> getAllBands(Principal principal) {
        return bandService.getAllBandsForUser(principal.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BandResponseDTO> getBandById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(bandService.getBandByIdForUser(id, principal.getName()));
    }

    @PostMapping
    public ResponseEntity<BandResponseDTO> createBand(@Valid @RequestBody BandRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(bandService.createBand(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BandResponseDTO> updateBand(@PathVariable Long id, @Valid @RequestBody BandRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(bandService.updateBand(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id, Principal principal) {
        bandService.deleteBand(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}