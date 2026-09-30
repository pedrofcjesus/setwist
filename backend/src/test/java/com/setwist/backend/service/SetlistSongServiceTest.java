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
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;

@ExtendWith(MockitoExtension.class)
class SetlistSongServiceTest {

    @Mock
    private SetlistRepository setlistRepository;

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SetlistSongService setlistSongService;

    private User user;
    private Setlist setlist;
    private Song song;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", "pedro@example.com", "password123");
        user.setId(1L);

        setlist = new Setlist();
        setlist.setId(1L);
        setlist.setName("Setlist Principal");
        setlist.setUser(user);

        song = new Song();
        song.setId(100L);
        song.setTitle("Song 1");
        song.setArtist("Band A");
        song.setUser(user);
    }

    @Test
    @DisplayName("Deve adicionar música à setlist se ambos pertencerem ao utilizador")
    void addSongToSetlist_Success() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO(100L, 1);

        when(setlistRepository.findByIdAndUserEmail(1L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));
        when(songRepository.findByIdAndUserEmail(100L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        SetlistResponseDTO result = setlistSongService.addSongToSetlist(1L, dto, "pedro@example.com");

        assertThat(result).isNotNull();
        verify(setlistRepository).save(setlist);
    }

    @Test
    @DisplayName("Deve recusar adicionar música se a música pertencer a outro utilizador")
    void addSongToSetlist_SongBelongsToOtherUser() {
        SetlistSongRequestDTO dto = new SetlistSongRequestDTO(999L, 1);

        when(setlistRepository.findByIdAndUserEmail(1L, "pedro@example.com"))
                .thenReturn(Optional.of(setlist));
        when(songRepository.findByIdAndUserEmail(999L, "pedro@example.com"))
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
