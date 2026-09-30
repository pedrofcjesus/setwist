package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class BandService {

    private final BandRepository bandRepository;
    private final UserRepository userRepository;

    public BandService(BandRepository bandRepository, UserRepository userRepository) {
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
    }

    public List<BandResponseDTO> getAllBandsForUser(String userEmail) {
        return bandRepository.findByUserEmail(userEmail)
                .stream()
                .map(BandResponseDTO::new)
                .toList();
    }

    public BandResponseDTO getBandByIdForUser(Long id, String userEmail) {
        Band band = bandRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));
        return new BandResponseDTO(band);
    }

    public BandResponseDTO createBand(BandRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", userEmail));

        Band band = new Band();
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());
        band.setUser(user);

        Band savedBand = bandRepository.save(band);
        return new BandResponseDTO(savedBand);
    }

    public BandResponseDTO updateBand(Long id, BandRequestDTO dto, String userEmail) {
        Band band = bandRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));

        band.setName(dto.getName());
        band.setDescription(dto.getDescription());

        Band updatedBand = bandRepository.save(band);
        return new BandResponseDTO(updatedBand);
    }

    public void deleteBand(Long id, String userEmail) {
        Band band = bandRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));
        
        bandRepository.delete(band);
    }

    public Band findBandEntityById(Long id) {
        return bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", id));
    }
}