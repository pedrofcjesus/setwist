package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.repository.BandRepository;

@Service
public class BandService {

    private final BandRepository bandRepository;

    public BandService(BandRepository bandRepository) {
        this.bandRepository = bandRepository;
    }

    public List<BandResponseDTO> getAllBands() {
        return bandRepository.findAll()
                .stream()
                .map(BandResponseDTO::new)
                .toList();
    }

    public BandResponseDTO getBandById(Long id) {
        Band band = findBandEntityById(id);
        return new BandResponseDTO(band);
    }

    public BandResponseDTO createBand(BandRequestDTO dto) {
        Band band = new Band();
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());

        Band savedBand = bandRepository.save(band);
        return new BandResponseDTO(savedBand);
    }

    public BandResponseDTO updateBand(Long id, BandRequestDTO dto) {
        Band band = findBandEntityById(id);
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());

        Band updatedBand = bandRepository.save(band);
        return new BandResponseDTO(updatedBand);
    }

    public void deleteBand(Long id) {
        if (!bandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banda", "id", id);
        }
        bandRepository.deleteById(id);
    }

    public Band findBandEntityById(Long id) {
        return bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));
    }
}