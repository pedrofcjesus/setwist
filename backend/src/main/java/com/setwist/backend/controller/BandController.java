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

import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.repository.BandRepository;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandRepository bandRepository;

    public BandController(BandRepository bandRepository) {
        this.bandRepository = bandRepository;
    }

    // 1. READ ALL - Listar todas as bandas
    @GetMapping
    public List<Band> getAllBands() {
        return bandRepository.findAll();
    }

    // 2. READ ONE - Ver 1 banda por ID
    @GetMapping("/{id}")
    public ResponseEntity<Band> getBandById(@PathVariable Long id) {
        Band band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));
        return ResponseEntity.ok(band);
    }

    // 3. CREATE - Criar nova banda
    @PostMapping
    public Band createBand(@RequestBody Band band) {
        return bandRepository.save(band);
    }

    // 4. UPDATE - Editar dados
    @PutMapping("/{id}")
    public ResponseEntity<Band> updateBand(@PathVariable Long id, @RequestBody Band bandDetails) {
        Band band = bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));

        band.setName(bandDetails.getName());
        band.setDescription(bandDetails.getDescription());

        Band updateBand = bandRepository.save(band);
        return ResponseEntity.ok(updateBand);
    }

    // 5. DELETE - Apagar banda
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id) {
        if (!bandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banda", "id", id);
        }

        bandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
