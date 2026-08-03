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
import com.setwist.backend.repository.BandRepository;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandRepository bandRepository_1;
    private final BandRepository bandRepository;

    public BandController(BandRepository bandRepository, BandRepository bandRepository_1) {
        this.bandRepository = bandRepository;
        this.bandRepository_1 = bandRepository_1;
    }

    // 1. READ ALL - Listar todas as bandas
    @GetMapping
    public List<Band> getAllBands() {
        return bandRepository.findAll();
    }

    // 2. READ ONE - Ver 1 banda por ID
    @GetMapping("/{id}")
    public ResponseEntity<Band> getBandById(@PathVariable Long id) {
        return bandRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREATE - Criar nova banda
    @PostMapping
    public Band createBand(@RequestBody Band band) {
        return bandRepository.save(band);
    }

    // 4. UPDATE - Editar dados
    @PutMapping("/{id}")
    public ResponseEntity<Band> updateBand(@PathVariable Long id, @RequestBody Band bandDetails) {
        return bandRepository.findById(id)
                .map(band -> {
                    band.setName(bandDetails.getName());
                    band.setDescription(bandDetails.getDescription());
                    Band updateBand = bandRepository.save(band);
                    return ResponseEntity.ok(updateBand);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. DELETE - Apagar banda
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id) {
        if (bandRepository.existsById(id)) {
            bandRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
