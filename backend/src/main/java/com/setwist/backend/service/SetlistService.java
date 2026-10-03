package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetlistService {

    private final SetlistRepository setlistRepository;
    private final BandRepository bandRepository;
    private final UserRepository userRepository;

    public SetlistService(SetlistRepository setlistRepository, BandRepository bandRepository, UserRepository userRepository) {
        this.setlistRepository = setlistRepository;
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
    }

    public List<SetlistResponseDTO> getAllSetlistsForUser(String userEmail) {
        return setlistRepository.findByUserEmail(userEmail)
                .stream()
                .map(SetlistResponseDTO::new)
                .toList();
    }

    public SetlistResponseDTO getSetlistByIdForUser(Long id, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", id));
        return new SetlistResponseDTO(setlist);
    }

    public SetlistResponseDTO createSetlist(SetlistRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", userEmail));

        Setlist setlist = new Setlist();
        setlist.setName(dto.getName());
        setlist.setDescription(dto.getDescription());
        setlist.setUser(user);

        if (dto.getBandId() != null) {
            Band band = bandRepository.findByIdAndUserEmail(dto.getBandId(), userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", dto.getBandId()));
            setlist.setBand(band);
        }

        Setlist savedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(savedSetlist);
    }

    @Transactional
    public SetlistResponseDTO updateSetlist(Long id, SetlistRequestDTO dto, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", id));

        setlist.setName(dto.getName());
        setlist.setDescription(dto.getDescription());

        if (dto.getBandId() != null) {
            Band band = bandRepository.findByIdAndUserEmail(dto.getBandId(), userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", dto.getBandId()));
            setlist.setBand(band);
        } else {
            setlist.setBand(null);
        }

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return new SetlistResponseDTO(updatedSetlist);
    }

    @Transactional
    public void deleteSetlist(Long id, String userEmail) {
        Setlist setlist = setlistRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", id));
        setlistRepository.delete(setlist);
    }
}