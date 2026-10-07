package com.setwist.backend.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.BandMemberRequestDTO;
import com.setwist.backend.dto.BandMemberResponseDTO;
import com.setwist.backend.dto.BandMemberUpdateDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class BandMemberService {

    private final BandMemberRepository bandMemberRepository;
    private final UserRepository userRepository;
    private final BandAccessService accessService;

    public BandMemberService(
            BandMemberRepository bandMemberRepository,
            UserRepository userRepository,
            BandAccessService accessService) {
        this.bandMemberRepository = bandMemberRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<BandMemberResponseDTO> getMembersByBand(Long bandId, String userEmail) {
        accessService.requireMembership(bandId, userEmail);

        return bandMemberRepository.findByBandId(bandId).stream()
                .sorted(Comparator.comparing((BandMember m) -> m.getRole() != BandRole.ADMIN)
                        .thenComparing(m -> m.getUser().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(m -> toDTO(m, userEmail))
                .toList();
    }

    @Transactional
    public BandMemberResponseDTO addMemberToBand(Long bandId, BandMemberRequestDTO dto, String adminEmail) {
        BandMember admin = accessService.requireAdmin(bandId, adminEmail);

        User userToAdd = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", dto.getUserEmail()));

        if (bandMemberRepository.existsByBandIdAndUserId(bandId, userToAdd.getId())) {
            throw new IllegalArgumentException("O utilizador já é membro desta banda.");
        }

        // Novos membros entram sempre como MEMBER (só há um admin: o criador)
        BandMember member = new BandMember(admin.getBand(), userToAdd, BandRole.MEMBER, clean(dto.getInstrument()));
        BandMember saved = bandMemberRepository.save(member);

        return toDTO(saved, adminEmail);
    }

    @Transactional
    public BandMemberResponseDTO updateMemberInstrument(Long bandId, Long userId, BandMemberUpdateDTO dto, String adminEmail) {
        accessService.requireAdmin(bandId, adminEmail);

        BandMember member = bandMemberRepository.findByBandIdAndUserId(bandId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de Banda", "userId", userId));

        member.setInstrument(clean(dto.getInstrument()));
        BandMember saved = bandMemberRepository.save(member);

        return toDTO(saved, adminEmail);
    }

    @Transactional
    public void removeMemberFromBand(Long bandId, Long userId, String adminEmail) {
        accessService.requireAdmin(bandId, adminEmail);

        BandMember member = bandMemberRepository.findByBandIdAndUserId(bandId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de Banda", "userId", userId));

        if (member.getRole() == BandRole.ADMIN) {
            throw new IllegalArgumentException("O administrador não pode ser removido da banda. Só pode apagá-la.");
        }

        // As sugestões do membro mantêm-se na lista (o admin pode retirá-las)
        bandMemberRepository.delete(member);
    }

    @Transactional
    public void leaveBand(Long bandId, String userEmail) {
        BandMember me = accessService.requireMembership(bandId, userEmail);

        if (me.getRole() == BandRole.ADMIN) {
            throw new IllegalArgumentException("O administrador não pode sair da banda. Só pode apagá-la.");
        }

        bandMemberRepository.delete(me);
    }

    private BandMemberResponseDTO toDTO(BandMember member, String currentUserEmail) {
        BandMemberResponseDTO dto = new BandMemberResponseDTO(member);
        dto.setCurrentUser(member.getUser() != null && currentUserEmail.equals(member.getUser().getEmail()));
        return dto;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}