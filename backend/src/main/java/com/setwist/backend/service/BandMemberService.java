package com.setwist.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.setwist.backend.dto.BandMemberRequestDTO;
import com.setwist.backend.dto.BandMemberResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class BandMemberService {

    private final BandMemberRepository bandMemberRepository;
    private final BandRepository bandRepository;
    private final UserRepository userRepository;

    public BandMemberService(
            BandMemberRepository bandMemberRepository,
            BandRepository bandRepository,
            UserRepository userRepository) {
        this.bandMemberRepository = bandMemberRepository;
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
    }

    public List<BandMemberResponseDTO> getMembersByBand(Long bandId) {
        return bandMemberRepository.findByBandId(bandId)
                .stream()
                .map(BandMemberResponseDTO::new)
                .toList();
    }

    public BandMemberResponseDTO addMemberToBand(Long bandId, BandMemberRequestDTO dto, String ownerEmail) {
        Band band = bandRepository.findByIdAndUserEmail(bandId, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));

        User userToAdd = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", dto.getUserEmail()));

        if (bandMemberRepository.existsByBandIdAndUserId(bandId, userToAdd.getId())) {
            throw new IllegalArgumentException("O utilizador já é membro desta banda.");
        }

        BandMember member = new BandMember(band, userToAdd, dto.getRole());
        BandMember savedMember = bandMemberRepository.save(member);

        return new BandMemberResponseDTO(savedMember);
    }

    public void removeMemberFromBand(Long bandId, Long userId, String ownerEmail) {
        // Valida se o utilizador atual é dono da banda
        bandRepository.findByIdAndUserEmail(bandId, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));

        BandMember member = bandMemberRepository.findByBandIdAndUserId(bandId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de Banda", "userId", userId));

        bandMemberRepository.delete(member);
    }
}