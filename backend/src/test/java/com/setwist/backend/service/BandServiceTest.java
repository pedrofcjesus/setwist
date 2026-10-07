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

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BandServiceTest {

    @Mock
    private BandRepository bandRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BandService bandService;

    private User user;
    private Band band;
    private Song song;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", "pedro@example.com", "password123");
        user.setId(1L);

        band = new Band();
        band.setId(5L);
        band.setName("Smoodies");
        band.setDescription("Projeto principal");
        band.setUser(user);

        song = new Song();
        song.setId(30L);
        song.setTitle("Superstition");
        song.setArtist("Stevie Wonder");
        song.setUser(user);
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

    @Test
    @DisplayName("Deve adicionar uma música do catálogo às sugestões da banda")
    void addSuggestionToBand_Success() {
        when(bandRepository.findByIdAndUserEmail(5L, "pedro@example.com"))
                .thenReturn(Optional.of(band));
        when(songRepository.findByIdAndUserEmail(30L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        bandService.addSuggestionToBand(5L, 30L, "pedro@example.com");

        assertThat(band.getSuggestions()).containsExactly(song);
        verify(bandRepository).save(band);
    }

    @Test
    @DisplayName("Deve recusar sugerir uma música que já está no pool da banda")
    void addSuggestionToBand_AlreadyInRepertoire_ThrowsException() {
        BandRepertoire entry = new BandRepertoire();
        entry.setBand(band);
        entry.setSong(song);
        band.getRepertoire().add(entry);

        when(bandRepository.findByIdAndUserEmail(5L, "pedro@example.com"))
                .thenReturn(Optional.of(band));
        when(songRepository.findByIdAndUserEmail(30L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        assertThatThrownBy(() -> bandService.addSuggestionToBand(5L, 30L, "pedro@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Esta música já está no repertório da banda.");

        assertThat(band.getSuggestions()).isEmpty();
        verify(bandRepository, never()).save(any(Band.class));
    }

    @Test
    @DisplayName("Deve promover uma sugestão para o pool e retirá-la das sugestões")
    void promoteSongToRepertoire_MovesSongFromSuggestionsToPool() {
        band.getSuggestions().add(song);

        when(bandRepository.findByIdAndUserEmail(5L, "pedro@example.com"))
                .thenReturn(Optional.of(band));
        when(songRepository.findByIdAndUserEmail(30L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        bandService.promoteSongToRepertoire(5L, 30L, "pedro@example.com");

        assertThat(band.getRepertoire()).hasSize(1);
        assertThat(band.getRepertoire().get(0).getSong()).isEqualTo(song);
        assertThat(band.getSuggestions()).isEmpty();
        verify(bandRepository).save(band);
    }
}
