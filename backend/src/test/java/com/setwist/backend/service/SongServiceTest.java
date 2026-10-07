package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.BandSuggestionRepository;
import com.setwist.backend.repository.SetlistSongRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BandRepertoireRepository bandRepertoireRepository;

    @Mock
    private SetlistSongRepository setlistSongRepository;

    @Mock
    private BandSuggestionRepository bandSuggestionRepository;

    @InjectMocks
    private SongService songService;

    private User user;
    private Song song;

    @BeforeEach
    void setUp() {
        user = new User("Pedro", "pedro@example.com", "password123");
        user.setId(1L);

        song = new Song();
        song.setId(20L);
        song.setTitle("Master of Puppets");
        song.setArtist("Metallica");
        song.setUser(user);
    }

    @Test
    @DisplayName("Deve devolver a música quando pertence ao utilizador")
    void getSongByIdForUser_Success() {
        when(songRepository.findByIdAndUserEmail(20L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        SongResponseDTO result = songService.getSongByIdForUser(20L, "pedro@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getTitle()).isEqualTo("Master of Puppets");
    }

    @Test
    @DisplayName("Deve devolver as bandas do pool e as bandas onde está apenas em sugestões")
    void getSongByIdForUser_IncludesPoolAndSuggestionBands() {
        when(songRepository.findByIdAndUserEmail(20L, "pedro@example.com"))
                .thenReturn(Optional.of(song));
        when(bandRepertoireRepository.findBandNamesBySongId(20L))
                .thenReturn(List.of("Smoodies"));
        when(bandSuggestionRepository.findBandNamesBySongId(20L))
                .thenReturn(List.of("Coffee Break"));

        SongResponseDTO result = songService.getSongByIdForUser(20L, "pedro@example.com");

        assertThat(result.getBands()).containsExactly("Smoodies");
        assertThat(result.getSuggestedInBands()).containsExactly("Coffee Break");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a música pertence a outro utilizador")
    void getSongByIdForUser_NotFoundOrUnauthorized() {
        when(songRepository.findByIdAndUserEmail(20L, "outro@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.getSongByIdForUser(20L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve criar uma nova música associada ao utilizador")
    void createSong_Success() {
        SongRequestDTO dto = new SongRequestDTO();
        dto.setTitle("Hysteria");
        dto.setArtist("Muse");

        when(userRepository.findByEmail("pedro@example.com")).thenReturn(Optional.of(user));
        when(songRepository.save(any(Song.class))).thenAnswer(invocation -> {
            Song saved = invocation.getArgument(0);
            saved.setId(21L);
            return saved;
        });

        SongResponseDTO result = songService.createSong(dto, "pedro@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(21L);
        assertThat(result.getTitle()).isEqualTo("Hysteria");
    }

    @Test
    @DisplayName("Deve eliminar a música e limpar setlists, pools e sugestões")
    void deleteSong_Success() {
        when(songRepository.findByIdAndUserEmail(20L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        songService.deleteSong(20L, "pedro@example.com");

        InOrder order = inOrder(setlistSongRepository, bandRepertoireRepository, bandSuggestionRepository, songRepository);
        order.verify(setlistSongRepository).deleteBySongId(20L);
        order.verify(bandRepertoireRepository).deleteBySongId(20L);
        order.verify(bandSuggestionRepository).deleteBySongId(20L);
        order.verify(songRepository).delete(song);
    }

    @Test
    @DisplayName("Não deve eliminar a música de outro utilizador")
    void deleteSong_Unauthorized() {
        when(songRepository.findByIdAndUserEmail(20L, "outro@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.deleteSong(20L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(songRepository, never()).delete(any());
        verify(bandSuggestionRepository, never()).deleteBySongId(any());
    }
}
