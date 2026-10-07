package com.setwist.backend.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.UserRepository;

@Service
public class SetlistService {

    private final SetlistRepository setlistRepository;
    private final UserRepository userRepository;
    private final BandAccessService accessService;

    public SetlistService(SetlistRepository setlistRepository, UserRepository userRepository, BandAccessService accessService) {
        this.setlistRepository = setlistRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<SetlistResponseDTO> getAllSetlistsForUser(String userEmail) {
        Set<Long> adminBandIds = accessService.getAdminBandIds(userEmail);

        return setlistRepository.findAccessibleByUserEmail(userEmail)
                .stream()
                .map(s -> toDTO(s, s.getBand() == null || adminBandIds.contains(s.getBand().getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public SetlistResponseDTO getSetlistByIdForUser(Long id, String userEmail) {
        Setlist setlist = accessService.requireSetlistView(id, userEmail);
        return toDTO(setlist, accessService.canEditSetlist(setlist, userEmail));
    }

    @Transactional
    public SetlistResponseDTO createSetlist(SetlistRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", "email", userEmail));

        Setlist setlist = new Setlist();
        setlist.setName(dto.getName());
        setlist.setDescription(dto.getDescription());
        setlist.setUser(user);

        // Só o admin da banda cria setlists para ela
        if (dto.getBandId() != null) {
            setlist.setBand(accessService.requireAdmin(dto.getBandId(), userEmail).getBand());
        }

        Setlist savedSetlist = setlistRepository.save(setlist);
        return toDTO(savedSetlist, true);
    }

    @Transactional
    public SetlistResponseDTO updateSetlist(Long id, SetlistRequestDTO dto, String userEmail) {
        Setlist setlist = accessService.requireSetlistEdit(id, userEmail);

        setlist.setName(dto.getName());
        setlist.setDescription(dto.getDescription());

        if (dto.getBandId() != null) {
            setlist.setBand(accessService.requireAdmin(dto.getBandId(), userEmail).getBand());
        } else {
            setlist.setBand(null);
        }

        Setlist updatedSetlist = setlistRepository.save(setlist);
        return toDTO(updatedSetlist, true);
    }

    @Transactional
    public void deleteSetlist(Long id, String userEmail) {
        Setlist setlist = accessService.requireSetlistEdit(id, userEmail);
        setlistRepository.delete(setlist);
    }

    private SetlistResponseDTO toDTO(Setlist setlist, boolean canEdit) {
        SetlistResponseDTO dto = new SetlistResponseDTO(setlist);
        dto.setCanEdit(canEdit);
        return dto;
    }
}