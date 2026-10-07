package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SetlistServiceTest {

    private static final String EMAIL = "pedro@example.com";

    @Mock
    private SetlistRepository setlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BandAccessService accessService;

    @InjectMocks
    private SetlistService setlistService;

    private User user;
    private Band band;
    private Setlist setlist;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", EMAIL, "password123");
        user.setId(1L);

        band = new Band();
        band.setId(5L);
        band.setName("Smoodies");

        setlist = new Setlist();
        setlist.setId(10L);
        setlist.setName("Ensaio Geral");
        setlist.setDescription("Lista para o ensaio de sábado");
        setlist.setUser(user);
        setlist.setBand(band);
    }

    @Test
    @DisplayName("Deve devolver a setlist com a indicação de edição quando o utilizador tem acesso")
    void getSetlistByIdForUser_Success() {
        when(accessService.requireSetlistView(10L, EMAIL)).thenReturn(setlist);
        when(accessService.canEditSetlist(setlist, EMAIL)).thenReturn(true);

        SetlistResponseDTO result = setlistService.getSetlistByIdForUser(10L, EMAIL);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Ensaio Geral");
        assertThat(result.isCanEdit()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção quando o utilizador não tem acesso à setlist")
    void getSetlistByIdForUser_NotAccessible() {
        when(accessService.requireSetlistView(10L, "outro@example.com"))
                .thenThrow(new ResourceNotFoundException("Setlist", "id", 10L));

        assertThatThrownBy(() -> setlistService.getSetlistByIdForUser(10L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar as setlists acessíveis, com canEdit só nas bandas onde é admin")
    void getAllSetlistsForUser_FlagsCanEdit() {
        Band otherBand = new Band();
        otherBand.setId(6L);
        otherBand.setName("Coffee Break");

        Setlist other = new Setlist();
        other.setId(11L);
        other.setName("Acústico");
        other.setBand(otherBand);
        other.setUser(user);

        when(accessService.getAdminBandIds(EMAIL)).thenReturn(Set.of(5L));
        when(setlistRepository.findAccessibleByUserEmail(EMAIL)).thenReturn(List.of(setlist, other));

        List<SetlistResponseDTO> result = setlistService.getAllSetlistsForUser(EMAIL);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isCanEdit()).isTrue();
        assertThat(result.get(1).isCanEdit()).isFalse();
    }

    @Test
    @DisplayName("O admin da banda cria uma setlist associada à banda")
    void createSetlist_Success() {
        SetlistRequestDTO dto = new SetlistRequestDTO();
        dto.setName("Concerto de Verão");
        dto.setDescription("Alinhamento para o festival");
        dto.setBandId(5L);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accessService.requireAdmin(5L, EMAIL)).thenReturn(new BandMember(band, user, BandRole.ADMIN));
        when(setlistRepository.save(any(Setlist.class))).thenAnswer(invocation -> {
            Setlist saved = invocation.getArgument(0);
            saved.setId(12L);
            return saved;
        });

        SetlistResponseDTO result = setlistService.createSetlist(dto, EMAIL);

        assertThat(result.getId()).isEqualTo(12L);
        assertThat(result.getName()).isEqualTo("Concerto de Verão");
        assertThat(result.getBand().getId()).isEqualTo(5L);
        assertThat(result.isCanEdit()).isTrue();
    }

    @Test
    @DisplayName("Um membro sem permissão não cria setlists para a banda")
    void createSetlist_NotAdmin() {
        SetlistRequestDTO dto = new SetlistRequestDTO();
        dto.setName("Concerto de Verão");
        dto.setBandId(5L);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(accessService.requireAdmin(5L, EMAIL)).thenThrow(new AccessDeniedException("sem permissão"));

        assertThatThrownBy(() -> setlistService.createSetlist(dto, EMAIL))
                .isInstanceOf(AccessDeniedException.class);

        verify(setlistRepository, never()).save(any(Setlist.class));
    }

    @Test
    @DisplayName("Deve eliminar a setlist quando o utilizador pode editá-la")
    void deleteSetlist_Success() {
        when(accessService.requireSetlistEdit(10L, EMAIL)).thenReturn(setlist);

        setlistService.deleteSetlist(10L, EMAIL);

        verify(setlistRepository).delete(setlist);
    }

    @Test
    @DisplayName("Não deve eliminar a setlist quando o utilizador não tem permissão")
    void deleteSetlist_Unauthorized() {
        when(accessService.requireSetlistEdit(10L, "ana@example.com"))
                .thenThrow(new AccessDeniedException("sem permissão"));

        assertThatThrownBy(() -> setlistService.deleteSetlist(10L, "ana@example.com"))
                .isInstanceOf(AccessDeniedException.class);

        verify(setlistRepository, never()).delete(any());
    }
}