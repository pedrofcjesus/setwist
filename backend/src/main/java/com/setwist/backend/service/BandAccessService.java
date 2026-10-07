package com.setwist.backend.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.SetlistRepository;

/**
 * Ponto único de autorização: quem é membro / admin de uma banda e quem pode ver / editar uma setlist.
 * Não-membros recebem 404 (não revela que a banda existe); membros sem permissão recebem 403.
 */
@Service
public class BandAccessService {

    private static final String ADMIN_ONLY_MESSAGE = "Apenas o administrador da banda pode realizar esta ação.";

    private final BandMemberRepository bandMemberRepository;
    private final SetlistRepository setlistRepository;

    public BandAccessService(BandMemberRepository bandMemberRepository, SetlistRepository setlistRepository) {
        this.bandMemberRepository = bandMemberRepository;
        this.setlistRepository = setlistRepository;
    }

    @Transactional(readOnly = true)
    public BandMember requireMembership(Long bandId, String email) {
        return bandMemberRepository.findByBandIdAndUserEmail(bandId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Banda", "id", bandId));
    }

    @Transactional(readOnly = true)
    public BandMember requireAdmin(Long bandId, String email) {
        BandMember member = requireMembership(bandId, email);
        if (member.getRole() != BandRole.ADMIN) {
            throw new AccessDeniedException(ADMIN_ONLY_MESSAGE);
        }
        return member;
    }

    @Transactional(readOnly = true)
    public Set<Long> getAdminBandIds(String email) {
        return bandMemberRepository.findByUserEmail(email).stream()
                .filter(m -> m.getRole() == BandRole.ADMIN)
                .map(m -> m.getBand().getId())
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Setlist requireSetlistView(Long setlistId, String email) {
        return requireSetlist(setlistId, email, false);
    }

    @Transactional(readOnly = true)
    public Setlist requireSetlistEdit(Long setlistId, String email) {
        return requireSetlist(setlistId, email, true);
    }

    @Transactional(readOnly = true)
    public boolean canEditSetlist(Setlist setlist, String email) {
        if (setlist.getBand() == null) {
            return setlist.getUser().getEmail().equals(email);
        }
        return bandMemberRepository.findByBandIdAndUserEmail(setlist.getBand().getId(), email)
                .map(m -> m.getRole() == BandRole.ADMIN)
                .orElse(false);
    }

    private Setlist requireSetlist(Long setlistId, String email, boolean edit) {
        Setlist setlist = setlistRepository.findById(setlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        // Setlist pessoal (sem banda): só o dono
        if (setlist.getBand() == null) {
            if (!setlist.getUser().getEmail().equals(email)) {
                throw new ResourceNotFoundException("Setlist", "id", setlistId);
            }
            return setlist;
        }

        BandMember member = bandMemberRepository.findByBandIdAndUserEmail(setlist.getBand().getId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Setlist", "id", setlistId));

        if (edit && member.getRole() != BandRole.ADMIN) {
            throw new AccessDeniedException(ADMIN_ONLY_MESSAGE);
        }
        return setlist;
    }
}