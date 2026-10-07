package com.setwist.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.setwist.backend.exception.ResourceNotFoundException;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.SetlistRepository;

@ExtendWith(MockitoExtension.class)
class BandAccessServiceTest {

    private static final String ADMIN_EMAIL = "pedro@example.com";
    private static final String MEMBER_EMAIL = "ana@example.com";

    @Mock
    private BandMemberRepository bandMemberRepository;

    @Mock
    private SetlistRepository setlistRepository;

    @InjectMocks
    private BandAccessService accessService;

    private User admin;
    private User ana;
    private Band band;

    @BeforeEach
    void setUp() {
        admin = new User("Pedro", ADMIN_EMAIL, "pw");
        admin.setId(1L);
        ana = new User("Ana", MEMBER_EMAIL, "pw");
        ana.setId(2L);

        band = new Band();
        band.setId(5L);
        band.setName("Smoodies");
        band.setUser(admin);
    }

    @Test
    @DisplayName("Quem não é membro recebe 404")
    void requireMembership_NotMember() {
        when(bandMemberRepository.findByBandIdAndUserEmail(5L, "outro@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accessService.requireMembership(5L, "outro@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Um membro sem ser admin não passa em requireAdmin")
    void requireAdmin_MemberIsDenied() {
        when(bandMemberRepository.findByBandIdAndUserEmail(5L, MEMBER_EMAIL))
                .thenReturn(Optional.of(new BandMember(band, ana, BandRole.MEMBER)));

        assertThatThrownBy(() -> accessService.requireAdmin(5L, MEMBER_EMAIL))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("O admin passa em requireAdmin")
    void requireAdmin_AdminPasses() {
        BandMember membership = new BandMember(band, admin, BandRole.ADMIN);
        when(bandMemberRepository.findByBandIdAndUserEmail(5L, ADMIN_EMAIL)).thenReturn(Optional.of(membership));

        assertThat(accessService.requireAdmin(5L, ADMIN_EMAIL)).isSameAs(membership);
    }

    @Test
    @DisplayName("Um membro pode ver a setlist da banda")
    void requireSetlistView_MemberCanView() {
        Setlist setlist = new Setlist();
        setlist.setId(10L);
        setlist.setBand(band);
        setlist.setUser(admin);

        when(setlistRepository.findById(10L)).thenReturn(Optional.of(setlist));
        when(bandMemberRepository.findByBandIdAndUserEmail(5L, MEMBER_EMAIL))
                .thenReturn(Optional.of(new BandMember(band, ana, BandRole.MEMBER)));

        assertThat(accessService.requireSetlistView(10L, MEMBER_EMAIL)).isSameAs(setlist);
    }

    @Test
    @DisplayName("Um membro não pode editar a setlist da banda")
    void requireSetlistEdit_MemberIsDenied() {
        Setlist setlist = new Setlist();
        setlist.setId(10L);
        setlist.setBand(band);
        setlist.setUser(admin);

        when(setlistRepository.findById(10L)).thenReturn(Optional.of(setlist));
        when(bandMemberRepository.findByBandIdAndUserEmail(5L, MEMBER_EMAIL))
                .thenReturn(Optional.of(new BandMember(band, ana, BandRole.MEMBER)));

        assertThatThrownBy(() -> accessService.requireSetlistEdit(10L, MEMBER_EMAIL))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Uma setlist sem banda só é visível ao dono")
    void requireSetlistView_PersonalSetlistOfAnotherUser() {
        Setlist setlist = new Setlist();
        setlist.setId(11L);
        setlist.setUser(admin);

        when(setlistRepository.findById(11L)).thenReturn(Optional.of(setlist));

        assertThatThrownBy(() -> accessService.requireSetlistView(11L, MEMBER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}