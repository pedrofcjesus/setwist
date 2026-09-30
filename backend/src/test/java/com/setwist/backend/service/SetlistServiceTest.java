package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.setwist.backend.dto.SetlistRequestDTO;
import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.UserRepository;
import com.setwist.backend.service.SetlistService;

@ExtendWith(MockitoExtension.class)
class SetlistServiceTest {

    @Mock
    private SetlistRepository setlistRepository;

    @Mock
    private BandRepository bandRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SetlistService setlistService;

    private User user;
    private Setlist setlist;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", "pedro@example.com", "password123");
        user.setId(1L);

        setlist = new Setlist();
        setlist.setId(10L);
        setlist.setName("Ensaio Geral");
        setlist.setDescription("Lista para o ensaio de sábado");
        setlist.setUser(user);
    }

    @Test
    @DisplayName("Deve devolver a setlist quando pertence ao utilizador autenticado")
    void getSetlistByIdForUser_Success() {
        when(setlistRepository.findByIdAndUserEmail(10L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));

        SetlistResponseDTO result = setlistService.getSetlistByIdForUser(10L, "pedro@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Ensaio Geral");
        verify(setlistRepository).findByIdAndUserEmail(10L, "pedro@example.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a setlist pertence a outro utilizador ou não existe")
    void getSetlistByIdForUser_NotFoundOrUnauthorized() {
        when(setlistRepository.findByIdAndUserEmail(10L, "outro@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> setlistService.getSetlistByIdForUser(10L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve criar uma nova setlist associada ao utilizador")
    void createSetlist_Success() {
        SetlistRequestDTO dto = new SetlistRequestDTO();
        dto.setName("Concerto de Verão");
        dto.setDescription("Alinhamento para o festival");

        when(userRepository.findByEmail("pedro@example.com")).thenReturn(Optional.of(user));
        when(setlistRepository.save(any(Setlist.class))).thenAnswer(invocation -> {
            Setlist saved = invocation.getArgument(0);
            saved.setId(11L);
            return saved;
        });

        SetlistResponseDTO result = setlistService.createSetlist(dto, "pedro@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(11L);
        assertThat(result.getName()).isEqualTo("Concerto de Verão");
        verify(setlistRepository).save(any(Setlist.class));
    }

    @Test
    @DisplayName("Deve eliminar a setlist quando o utilizador é o proprietário")
    void deleteSetlist_Success() {
        when(setlistRepository.findByIdAndUserEmail(10L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));

        setlistService.deleteSetlist(10L, "pedro@example.com");

        verify(setlistRepository).delete(setlist);
    }

    @Test
    @DisplayName("Não deve eliminar a setlist quando pertence a outro utilizador")
    void deleteSetlist_Unauthorized() {
        when(setlistRepository.findByIdAndUserEmail(10L, "hacker@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> setlistService.deleteSetlist(10L, "hacker@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(setlistRepository, never()).delete(any());
    }
}