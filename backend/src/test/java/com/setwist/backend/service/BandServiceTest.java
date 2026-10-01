package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BandServiceTest {

    @Mock
    private BandRepository bandRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BandMemberRepository bandMemberRepository;

    @InjectMocks
    private BandService bandService;

    private User user;
    private Band band;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", "pedro@example.com", "password123");
        user.setId(1L);

        band = new Band();
        band.setId(5L);
        band.setName("Smoodies");
        band.setDescription("Projeto principal");
        band.setUser(user);
    }

    @Test
    @DisplayName("Deve devolver a banda quando pertence ao utilizador")
    void getBandByIdForUser_Success() {
        when(bandRepository.findByIdAndUserEmail(5L, "pedro@example.com"))
                .thenReturn(Optional.of(band));

        BandResponseDTO result = bandService.getBandByIdForUser(5L, "pedro@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Smoodies");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a banda pertence a outro utilizador ou não existe")
    void getBandByIdForUser_NotFoundOrUnauthorized() {
        when(bandRepository.findByIdAndUserEmail(5L, "outro@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bandService.getBandByIdForUser(5L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve criar uma nova banda associada ao utilizador")
    void createBand_Success() {
        BandRequestDTO dto = new BandRequestDTO();
        dto.setName("Coffee Break");
        dto.setDescription("Duo acústico");

        when(userRepository.findByEmail("pedro@example.com")).thenReturn(Optional.of(user));
        when(bandRepository.save(any(Band.class))).thenAnswer(invocation -> {
            Band saved = invocation.getArgument(0);
            saved.setId(6L);
            return saved;
        });

        when(bandMemberRepository.save(any(BandMember.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        BandResponseDTO result = bandService.createBand(dto, "pedro@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(6L);
        assertThat(result.getName()).isEqualTo("Coffee Break");
    }

    @Test
    @DisplayName("Deve eliminar a banda quando o utilizador é o proprietário")
    void deleteBand_Success() {
        when(bandRepository.findByIdAndUserEmail(5L, "pedro@example.com"))
                .thenReturn(Optional.of(band));

        bandService.deleteBand(5L, "pedro@example.com");

        verify(bandRepository).delete(band);
    }
}
