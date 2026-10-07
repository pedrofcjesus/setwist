package com.setwist.backend.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.dto.BandSuggestionResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.BandSuggestion;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;
import com.setwist.backend.util.SongMatcher;

@Service
public class BandService {

    private final BandRepository bandRepository;
    private final BandMemberRepository bandMemberRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final BandAccessService accessService;

    public BandService(
            BandRepository bandRepository,
            BandMemberRepository bandMemberRepository,
            SongRepository songRepository,
            UserRepository userRepository,
            BandAccessService accessService) {
        this.bandRepository = bandRepository;
        this.bandMemberRepository = bandMemberRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    // --- CRUD DE BANDAS ---

    @Transactional(readOnly = true)
    public List<BandResponseDTO> getAllBandsForUser(String userEmail) {
        return bandMemberRepository.findByUserEmail(userEmail).stream()
                .map(m -> mapToBandDTO(m.getBand(), m))
                .sorted(Comparator.comparing(BandResponseDTO::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public BandResponseDTO getBandByIdForUser(Long bandId, String userEmail) {
        BandMember me = accessService.requireMembership(bandId, userEmail);
        return mapToBandDTO(me.getBand(), me);
    }

    @Transactional
    public BandResponseDTO createBand(BandRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Band band = new Band(dto.getName(), dto.getDescription());
        band.setUser(user);

        // O criador é sempre ADMIN e membro da banda
        BandMember adminMembership = new BandMember(band, user, BandRole.ADMIN, clean(dto.getInstrument()));
        band.getMembers().add(adminMembership);

        Band savedBand = bandRepository.save(band);
        return mapToBandDTO(savedBand, adminMembership);
    }

    @Transactional
    public BandResponseDTO updateBand(Long bandId, BandRequestDTO dto, String userEmail) {
        BandMember admin = accessService.requireAdmin(bandId, userEmail);
        Band band = admin.getBand();
        band.setName(dto.getName());
        band.setDescription(dto.getDescription());

        Band updatedBand = bandRepository.save(band);
        return mapToBandDTO(updatedBand, admin);
    }

    @Transactional
    public void deleteBand(Long bandId, String userEmail) {
        BandMember admin = accessService.requireAdmin(bandId, userEmail);
        bandRepository.delete(admin.getBand());
    }

    // --- GESTÃO DE SUGESTÕES ---

    @Transactional(readOnly = true)
    public List<BandSuggestionResponseDTO> getSuggestionsForBand(Long bandId, String userEmail) {
        BandMember me = accessService.requireMembership(bandId, userEmail);
        boolean isAdmin = me.getRole() == BandRole.ADMIN;

        return me.getBand().getSuggestions().stream()
                .map(s -> {
                    boolean own = isAuthor(s, userEmail);
                    return new BandSuggestionResponseDTO(s, own, isAdmin || own);
                })
                .toList();
    }

    @Transactional
    public void addSuggestionToBand(Long bandId, Long songId, String userEmail) {
        BandMember me = accessService.requireMembership(bandId, userEmail);
        Band band = me.getBand();

        // Cada membro sugere músicas do seu próprio catálogo
        Song song = songRepository.findByIdAndUserEmail(songId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));

        boolean inRepertoire = band.getRepertoire().stream()
                .anyMatch(r -> SongMatcher.isSameSong(r.getSong(), song));
        if (inRepertoire) {
            throw new IllegalArgumentException("Esta música já está no repertório da banda.");
        }

        boolean alreadySuggested = band.getSuggestions().stream()
                .anyMatch(s -> SongMatcher.isSameSong(s.getSong(), song));
        if (alreadySuggested) {
            throw new IllegalArgumentException("Esta música já está nas sugestões da banda.");
        }

        band.getSuggestions().add(new BandSuggestion(band, song, me.getUser()));
        bandRepository.save(band);
    }

    @Transactional
    public void removeSuggestionFromBand(Long bandId, Long songId, String userEmail) {
        BandMember me = accessService.requireMembership(bandId, userEmail);
        Band band = me.getBand();

        BandSuggestion suggestion = findSuggestion(band, songId);

        boolean isAdmin = me.getRole() == BandRole.ADMIN;
        if (!isAdmin && !isAuthor(suggestion, userEmail)) {
            throw new AccessDeniedException("Só podes retirar as sugestões que adicionaste.");
        }

        band.getSuggestions().remove(suggestion);
        bandRepository.save(band);
    }

    @Transactional
    public void promoteSongToRepertoire(Long bandId, Long songId, String userEmail) {
        BandMember admin = accessService.requireAdmin(bandId, userEmail);
        Band band = admin.getBand();

        BandSuggestion suggestion = findSuggestion(band, songId);
        Song source = suggestion.getSong();

        // 1. Adiciona ao pool (se ainda não existir uma música igual)
        boolean alreadyInRepertoire = band.getRepertoire().stream()
                .anyMatch(r -> SongMatcher.isSameSong(r.getSong(), source));

        if (!alreadyInRepertoire) {
            Song target = resolveSongForAdmin(source, admin.getUser(), userEmail);

            BandRepertoire entry = new BandRepertoire();
            entry.setBand(band);
            entry.setSong(target);
            band.getRepertoire().add(entry);
        }

        // 2. Remove a sugestão
        band.getSuggestions().remove(suggestion);

        bandRepository.save(band);
    }

    // --- MÉTODOS AUXILIARES E MAPEAMENTOS ---

    // A música do pool tem de pertencer ao catálogo do admin: reaproveita uma igual ou cria cópia
    private Song resolveSongForAdmin(Song source, User adminUser, String adminEmail) {
        if (source.getUser() != null && Objects.equals(source.getUser().getId(), adminUser.getId())) {
            return source;
        }
        return songRepository.findByUserEmail(adminEmail).stream()
                .filter(s -> SongMatcher.isSameSong(s, source))
                .findFirst()
                .orElseGet(() -> songRepository.save(new Song(source.getTitle(), source.getArtist(), adminUser)));
    }

    private BandSuggestion findSuggestion(Band band, Long songId) {
        return band.getSuggestions().stream()
                .filter(s -> s.getSong() != null && s.getSong().getId().equals(songId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sugestão", "songId", songId));
    }

    private boolean isAuthor(BandSuggestion suggestion, String userEmail) {
        return suggestion.getAddedBy() != null && userEmail.equals(suggestion.getAddedBy().getEmail());
    }

    private BandResponseDTO mapToBandDTO(Band band, BandMember me) {
        BandResponseDTO dto = new BandResponseDTO(band);
        dto.setCurrentUserRole(me.getRole());
        dto.setCurrentUserInstrument(me.getInstrument());
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