package com.setwist.backend.controller;

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

import com.setwist.backend.model.Band;
import com.setwist.backend.service.BandService;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping
    public List<Band> getAllBands() {
        return bandService.getAllBands();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Band> getBAndById(@PathVariable Long id) {
        return ResponseEntity.ok(bandService.getBandById(id));
    }

    @PostMapping
    public ResponseEntity<Band> createBand(@RequestBody Band band) {
        return ResponseEntity.ok(bandService.createBand(band));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Band> updateBand(@PathVariable Long id, @RequestBody Band bandDetails) {
        return ResponseEntity.ok(bandService.updateBand(id, bandDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id) {
        bandService.deleteBand(id);
        return ResponseEntity.noContent().build();
    }

}
