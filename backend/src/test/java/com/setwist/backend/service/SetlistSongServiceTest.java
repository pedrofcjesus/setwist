package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.setwist.backend.dto.SetlistResponseDTO;
import com.setwist.backend.dto.SetlistSongReorderDTO;
import com.setwist.backend.dto.SetlistSongRequestDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.SetlistRepository;

@ExtendWith(MockitoExtension.class)
class SetlistSongServiceTest {

    private static final String EMAIL = "pedro@example.com";

    @Mock
    private SetlistRepository setlistRepository;

    @Mock
    private BandRepertoireRepository bandRepertoireRepository;

    @Mock
    private BandAccessService accessService;

    @InjectMocks
    private SetlistSongService setlistSongService;

    private Band band;
    private Setlist setlist;
    private BandRepertoire repertoireItem;

    @BeforeEach
    void setUp() {
        User user = new User("Pedro", EMAIL, "password123");
        user.setId(1L);

        band = new Band();
        band.setId(1L);
        band.setName("Smoodies");

        setlist = new Setlist();
        setlist.setId(1L);
        setlist.setName("Setlist Principal");
        setlist.setUser(user);
        setlist.setBand(band);

        repertoireItem = new BandRepertoire();
        repertoireItem.setId(100L);
        repertoireItem.setBand(band);
    }

    @Test
    @DisplayName("Deve adicionar música à setlist se pertencer ao repertório da banda")
    void addSongToSetlist_Success() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO();
        dto.setRepertoireItemId(100L);
        dto.setPosition(1);

        when(accessService.requireSetlistEdit(1L, EMAIL)).thenReturn(setlist);
        when(bandRepertoireRepository.findByIdAndBandId(100L, 1L)).thenReturn(Optional.of(repertoireItem));
        when(setlistRepository.saveAndFlush(any(Setlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SetlistResponseDTO result = setlistSongService.addSongToSetlist(1L, dto, EMAIL);

        assertThat(result.getSetlistSongs()).hasSize(1);
        assertThat(result.isCanEdit()).isTrue();
        verify(setlistRepository).saveAndFlush(setlist);
    }

    @Test
    @DisplayName("Deve recusar adicionar um item que não pertence ao repertório da banda da setlist")
    void addSongToSetlist_RepertoireItemNotInBand() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO();
        dto.setRepertoireItemId(999L);
        dto.setPosition(1);

        when(accessService.requireSetlistEdit(1L, EMAIL)).thenReturn(setlist);
        when(bandRepertoireRepository.findByIdAndBandId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> setlistSongService.addSongToSetlist(1L, dto, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(setlistRepository, never()).saveAndFlush(any(Setlist.class));
    }

    @Test
    @DisplayName("Um membro sem permissão não pode adicionar músicas à setlist")
    void addSongToSetlist_NotAdmin() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO();
        dto.setRepertoireItemId(100L);

        when(accessService.requireSetlistEdit(1L, "ana@example.com"))
                .thenThrow(new AccessDeniedException("sem permissão"));

        assertThatThrownBy(() -> setlistSongService.addSongToSetlist(1L, dto, "ana@example.com"))
                .isInstanceOf(AccessDeniedException.class);

        verify(setlistRepository, never()).saveAndFlush(any(Setlist.class));
    }

    @Test
    @DisplayName("Deve remover música da setlist e reajustar posições")
    void removeSongFromSetlist_Success() {
        setlist.getSetlistSongs().add(new SetlistSong(setlist, repertoireItem, 1));

        when(accessService.requireSetlistEdit(1L, EMAIL)).thenReturn(setlist);
        when(setlistRepository.save(any(Setlist.class))).thenReturn(setlist);

        SetlistResponseDTO result = setlistSongService.removeSongFromSetlist(1L, 100L, EMAIL);

        assertThat(result.getSetlistSongs()).isEmpty();
        verify(setlistRepository).save(setlist);
    }

    @Test
    @DisplayName("Deve reordenar as músicas da setlist")
    void reorderSongs_Success() {
        BandRepertoire second = new BandRepertoire();
        second.setId(200L);
        second.setBand(band);

        setlist.getSetlistSongs().add(new SetlistSong(setlist, repertoireItem, 1));
        setlist.getSetlistSongs().add(new SetlistSong(setlist, second, 2));

        when(accessService.requireSetlistEdit(1L, EMAIL)).thenReturn(setlist);
        when(setlistRepository.save(any(Setlist.class))).thenReturn(setlist);

        SetlistResponseDTO result = setlistSongService.reorderSongs(1L, new SetlistSongReorderDTO(List.of(200L, 100L)), EMAIL);

        assertThat(result.getSetlistSongs().get(0).getRepertoireItem().getId()).isEqualTo(200L);
        assertThat(result.getSetlistSongs().get(1).getRepertoireItem().getId()).isEqualTo(100L);
    }
}