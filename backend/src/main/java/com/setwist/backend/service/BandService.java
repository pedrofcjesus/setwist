package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.repository.BandRepository;

@Service
public class BandService {

    private final BandRepository bandRepository;

    public BandService(BandRepository bandRepository) {
        this.bandRepository = bandRepository;
    }

    public List<Band> getAllBands() {
        return bandRepository.findAll();
    }

    public Band getBandById(Long id) {
        return bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));
    }

    // Aceita BandRequestDTO para criar a banda
    public Band createBand(BandRequestDTO dto) {
        Band band = new Band();
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());
        return bandRepository.save(band);
    }

    // Aceita BandRequestDTO para atualizar a banda
    public Band updateBand(Long id, BandRequestDTO dto) {
        Band band = getBandById(id);
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());
        return bandRepository.save(band);
    }

    public void deleteBand(Long id) {
        if (!bandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banda", "id", id);
        }
        bandRepository.deleteById(id);
    }
}