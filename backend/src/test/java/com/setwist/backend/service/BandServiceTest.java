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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.setwist.backend.dto.BandRequestDTO;
import com.setwist.backend.dto.BandResponseDTO;
import com.setwist.backend.dto.BandSuggestionResponseDTO;
import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.BandSuggestion;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BandServiceTest {

    private static final String ADMIN_EMAIL = "pedro@example.com";
    private static final String MEMBER_EMAIL = "ana@example.com";

    @Mock
    private BandRepository bandRepository;

    @Mock
    private BandMemberRepository bandMemberRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BandAccessService accessService;

    @InjectMocks
    private BandService bandService;

    private User admin;
    private User ana;
    private Band band;
    private BandMember adminMembership;
    private BandMember anaMembership;
    private Song adminSong;
    private Song anaSong;

    @BeforeEach
    void setUp() {
        admin = new User("Pedro", ADMIN_EMAIL, "pw");
        admin.setId(1L);
        ana = new User("Ana", MEMBER_EMAIL, "pw");
        ana.setId(2L);

        band = new Band();
        band.setId(5L);
        band.setName("Smoodies");
        band.setDescription("Projeto principal");
        band.setUser(admin);

        adminMembership = new BandMember(band, admin, BandRole.ADMIN, "Baixo");
        anaMembership = new BandMember(band, ana, BandRole.MEMBER, "Voz");

        adminSong = new Song("Superstition", "Stevie Wonder", admin);
        adminSong.setId(30L);
        anaSong = new Song("Valerie", "Amy Winehouse", ana);
        anaSong.setId(31L);
    }

    private BandSuggestion suggest(Song song, User by) {
        BandSuggestion suggestion = new BandSuggestion(band, song, by);
        band.getSuggestions().add(suggestion);
        return suggestion;
    }

    // --- Bandas ---

    @Test
    @DisplayName("Deve devolver a banda com a permissão do utilizador quando é membro")
    void getBandByIdForUser_Success() {
        when(accessService.requireMembership(5L, ADMIN_EMAIL)).thenReturn(adminMembership);

        BandResponseDTO result = bandService.getBandByIdForUser(5L, ADMIN_EMAIL);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Smoodies");
        assertThat(result.getCurrentUserRole()).isEqualTo(BandRole.ADMIN);
        assertThat(result.getCurrentUserInstrument()).isEqualTo("Baixo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o utilizador não é membro da banda")
    void getBandByIdForUser_NotMember() {
        when(accessService.requireMembership(5L, "outro@example.com"))
                .thenThrow(new ResourceNotFoundException("Banda", "id", 5L));

        assertThatThrownBy(() -> bandService.getBandByIdForUser(5L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve criar a banda com o criador como ADMIN e membro")
    void createBand_CreatorBecomesAdmin() {
        BandRequestDTO dto = new BandRequestDTO();
        dto.setName("Coffee Break");
        dto.setDescription("Duo acústico");
        dto.setInstrument("  Guitarra  ");

        when(userRepository.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(admin));
        when(bandRepository.save(any(Band.class))).thenAnswer(invocation -> {
            Band saved = invocation.getArgument(0);
            saved.setId(6L);
            return saved;
        });

        BandResponseDTO result = bandService.createBand(dto, ADMIN_EMAIL);

        assertThat(result.getId()).isEqualTo(6L);
        assertThat(result.getCurrentUserRole()).isEqualTo(BandRole.ADMIN);
        assertThat(result.getCurrentUserInstrument()).isEqualTo("Guitarra");

        ArgumentCaptor<Band> captor = ArgumentCaptor.forClass(Band.class);
        verify(bandRepository).save(captor.capture());
        assertThat(captor.getValue().getMembers()).hasSize(1);
        assertThat(captor.getValue().getMembers().get(0).getRole()).isEqualTo(BandRole.ADMIN);
        assertThat(captor.getValue().getMembers().get(0).getUser()).isEqualTo(admin);
    }

    @Test
    @DisplayName("Deve eliminar a banda quando o utilizador é administrador")
    void deleteBand_Success() {
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);

        bandService.deleteBand(5L, ADMIN_EMAIL);

        verify(bandRepository).delete(band);
    }

    @Test
    @DisplayName("Não deve eliminar a banda quando o utilizador não é administrador")
    void deleteBand_NotAdmin() {
        when(accessService.requireAdmin(5L, MEMBER_EMAIL)).thenThrow(new AccessDeniedException("sem permissão"));

        assertThatThrownBy(() -> bandService.deleteBand(5L, MEMBER_EMAIL))
                .isInstanceOf(AccessDeniedException.class);

        verify(bandRepository, never()).delete(any(Band.class));
    }

    // --- Sugestões ---

    @Test
    @DisplayName("Um membro deve poder sugerir uma música do seu catálogo")
    void addSuggestion_Success() {
        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);
        when(songRepository.findByIdAndUserEmail(31L, MEMBER_EMAIL)).thenReturn(Optional.of(anaSong));

        bandService.addSuggestionToBand(5L, 31L, MEMBER_EMAIL);

        assertThat(band.getSuggestions()).hasSize(1);
        assertThat(band.getSuggestions().get(0).getAddedBy()).isEqualTo(ana);
        verify(bandRepository).save(band);
    }

    @Test
    @DisplayName("Deve recusar sugerir uma música igual (título/artista) a uma do pool")
    void addSuggestion_AlreadyInRepertoire() {
        Song poolSong = new Song("valerie", "AMY WINEHOUSE", admin);
        poolSong.setId(40L);
        band.getRepertoire().add(new BandRepertoire(band, poolSong, null, null, null));

        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);
        when(songRepository.findByIdAndUserEmail(31L, MEMBER_EMAIL)).thenReturn(Optional.of(anaSong));

        assertThatThrownBy(() -> bandService.addSuggestionToBand(5L, 31L, MEMBER_EMAIL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Esta música já está no repertório da banda.");

        assertThat(band.getSuggestions()).isEmpty();
        verify(bandRepository, never()).save(any(Band.class));
    }

    @Test
    @DisplayName("Deve recusar sugerir uma música que já está nas sugestões")
    void addSuggestion_AlreadySuggested() {
        suggest(new Song("Valerie", "Amy Winehouse", admin), admin);

        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);
        when(songRepository.findByIdAndUserEmail(31L, MEMBER_EMAIL)).thenReturn(Optional.of(anaSong));

        assertThatThrownBy(() -> bandService.addSuggestionToBand(5L, 31L, MEMBER_EMAIL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Esta música já está nas sugestões da banda.");

        verify(bandRepository, never()).save(any(Band.class));
    }

    @Test
    @DisplayName("Um membro deve poder retirar a sua própria sugestão")
    void removeSuggestion_MemberRemovesOwn() {
        suggest(anaSong, ana);
        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);

        bandService.removeSuggestionFromBand(5L, 31L, MEMBER_EMAIL);

        assertThat(band.getSuggestions()).isEmpty();
        verify(bandRepository).save(band);
    }

    @Test
    @DisplayName("Um membro não deve poder retirar a sugestão de outro")
    void removeSuggestion_MemberCannotRemoveOthers() {
        suggest(adminSong, admin);
        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);

        assertThatThrownBy(() -> bandService.removeSuggestionFromBand(5L, 30L, MEMBER_EMAIL))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(band.getSuggestions()).hasSize(1);
        verify(bandRepository, never()).save(any(Band.class));
    }

    @Test
    @DisplayName("O administrador deve poder retirar a sugestão de qualquer membro")
    void removeSuggestion_AdminRemovesAny() {
        suggest(anaSong, ana);
        when(accessService.requireMembership(5L, ADMIN_EMAIL)).thenReturn(adminMembership);

        bandService.removeSuggestionFromBand(5L, 31L, ADMIN_EMAIL);

        assertThat(band.getSuggestions()).isEmpty();
        verify(bandRepository).save(band);
    }

    @Test
    @DisplayName("Deve marcar canEdit/canRemove consoante o autor da sugestão")
    void getSuggestions_FlagsPerUser() {
        suggest(anaSong, ana);
        suggest(adminSong, admin);
        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);

        List<BandSuggestionResponseDTO> result = bandService.getSuggestionsForBand(5L, MEMBER_EMAIL);

        BandSuggestionResponseDTO own = result.stream().filter(s -> s.getSongId().equals(31L)).findFirst().orElseThrow();
        BandSuggestionResponseDTO other = result.stream().filter(s -> s.getSongId().equals(30L)).findFirst().orElseThrow();

        assertThat(own.isCanEdit()).isTrue();
        assertThat(own.isCanRemove()).isTrue();
        assertThat(own.getAddedByName()).isEqualTo("Ana");
        assertThat(other.isCanEdit()).isFalse();
        assertThat(other.isCanRemove()).isFalse();
    }

    // --- Promoção ---

    @Test
    @DisplayName("Ao promover uma música de um membro, cria cópia no catálogo do admin e retira a sugestão")
    void promote_CopiesSongToAdminCatalog() {
        suggest(anaSong, ana);
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(songRepository.findByUserEmail(ADMIN_EMAIL)).thenReturn(List.of(adminSong));
        when(songRepository.save(any(Song.class))).thenAnswer(invocation -> {
            Song saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        bandService.promoteSongToRepertoire(5L, 31L, ADMIN_EMAIL);

        assertThat(band.getRepertoire()).hasSize(1);
        Song inPool = band.getRepertoire().get(0).getSong();
        assertThat(inPool.getId()).isEqualTo(99L);
        assertThat(inPool.getUser()).isEqualTo(admin);
        assertThat(inPool.getTitle()).isEqualTo("Valerie");
        assertThat(band.getSuggestions()).isEmpty();
        verify(bandRepository).save(band);
    }

    @Test
    @DisplayName("Ao promover, reaproveita uma música igual que o admin já tenha")
    void promote_ReusesAdminSongWithSameTitleAndArtist() {
        suggest(anaSong, ana);
        Song existing = new Song("valerie", " amy winehouse ", admin);
        existing.setId(50L);

        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(songRepository.findByUserEmail(ADMIN_EMAIL)).thenReturn(List.of(existing));

        bandService.promoteSongToRepertoire(5L, 31L, ADMIN_EMAIL);

        assertThat(band.getRepertoire()).hasSize(1);
        assertThat(band.getRepertoire().get(0).getSong()).isSameAs(existing);
        assertThat(band.getSuggestions()).isEmpty();
        verify(songRepository, never()).save(any(Song.class));
    }

    @Test
    @DisplayName("Ao promover uma sugestão do próprio admin, não copia a música")
    void promote_AdminOwnSong() {
        suggest(adminSong, admin);
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);

        bandService.promoteSongToRepertoire(5L, 30L, ADMIN_EMAIL);

        assertThat(band.getRepertoire()).hasSize(1);
        assertThat(band.getRepertoire().get(0).getSong()).isSameAs(adminSong);
        assertThat(band.getSuggestions()).isEmpty();
        verify(songRepository, never()).findByUserEmail(any());
        verify(songRepository, never()).save(any(Song.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao promover uma música que não está nas sugestões")
    void promote_NotInSuggestions() {
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);

        assertThatThrownBy(() -> bandService.promoteSongToRepertoire(5L, 31L, ADMIN_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bandRepository, never()).save(any(Band.class));
    }

    @Test
    @DisplayName("Um membro não deve poder promover sugestões")
    void promote_NotAdmin() {
        when(accessService.requireAdmin(5L, MEMBER_EMAIL)).thenThrow(new AccessDeniedException("sem permissão"));

        assertThatThrownBy(() -> bandService.promoteSongToRepertoire(5L, 31L, MEMBER_EMAIL))
                .isInstanceOf(AccessDeniedException.class);
    }
}