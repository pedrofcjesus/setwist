package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public Band createBand(Band band) {
        return bandRepository.save(band);
    }

    public Band updateBand(Long id, Band bandDetails) {
        Band band = getBandById(id);
        band.setName(bandDetails.getName());
        band.setDescription(bandDetails.getDescription());
        return bandRepository.save(band);
    }

    public void deleteBand(Long id) {
        if (!bandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banda", "id", id);
        }
        bandRepository.deleteById(id);
    }
}
