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

import com.setwist.backend.dto.SongRequestDTO;
import com.setwist.backend.dto.SongResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BandRepository bandRepository;

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
    @DisplayName("Deve eliminar a música quando o utilizador é o proprietário")
    void deleteSong_Success() {
        when(songRepository.findByIdAndUserEmail(20L, "pedro@example.com"))
                .thenReturn(Optional.of(song));

        songService.deleteSong(20L, "pedro@example.com");

        verify(songRepository).delete(song);
    }
}
