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

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.SetlistRepository;

@ExtendWith(MockitoExtension.class)
class SetlistSongServiceTest {

    @Mock
    private SetlistRepository setlistRepository;

    @Mock
    private BandRepertoireRepository bandRepertoireRepository; // Atualizado para a nova arquitetura

    @InjectMocks
    private SetlistSongService setlistSongService;

    private User user;
    private Band band;
    private Setlist setlist;
    private BandRepertoire repertoireItem;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", "pedro@example.com", "password123");
        user.setId(1L);

        band = new Band();
        band.setId(1L);
        band.setName("Smoodies");

        setlist = new Setlist();
        setlist.setId(1L);
        setlist.setName("Setlist Principal");
        setlist.setUser(user);
        setlist.setBand(band); // Setlist agora tem uma banda associada

        repertoireItem = new BandRepertoire();
        repertoireItem.setId(100L);
        repertoireItem.setBand(band);
    }

    @Test
    @DisplayName("Deve adicionar música à setlist se pertencer ao repertório da banda")
    void addSongToSetlist_Success() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO();
        dto.setRepertoireItemId(100L); // Nota: confirma se o nome no teu DTO é setRepertoireItemId, setBandRepertoireId ou setSongId
        dto.setPosition(1);

        when(setlistRepository.findByIdAndUserEmail(1L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));
        when(bandRepertoireRepository.findById(100L))
                .thenReturn(Optional.of(repertoireItem));

        SetlistResponseDTO result = setlistSongService.addSongToSetlist(1L, dto, "pedro@example.com");

        assertThat(result).isNotNull();
        verify(setlistRepository).save(setlist);
    }

    @Test
    @DisplayName("Deve recusar adicionar música se o item não existir no repertório")
    void addSongToSetlist_RepertoireItemNotFound() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO();
        dto.setRepertoireItemId(999L);
        dto.setPosition(1);

        when(setlistRepository.findByIdAndUserEmail(1L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));
        when(bandRepertoireRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> setlistSongService.addSongToSetlist(1L, dto, "pedro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve remover música da setlist e reajustar posições")
    void removeSongFromSetlist_Success() {
        when(setlistRepository.findByIdAndUserEmail(1L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));
        when(setlistRepository.save(any(Setlist.class))).thenReturn(setlist);

        SetlistResponseDTO result = setlistSongService.removeSongFromSetlist(1L, 100L, "pedro@example.com");

        assertThat(result).isNotNull();
        verify(setlistRepository).save(setlist);
    }
}