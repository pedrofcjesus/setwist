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

import com.setwist.backend.dto.BandMemberRequestDTO;
import com.setwist.backend.dto.BandMemberResponseDTO;
import com.setwist.backend.dto.BandMemberUpdateDTO;
import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BandMemberServiceTest {

    private static final String ADMIN_EMAIL = "pedro@example.com";
    private static final String MEMBER_EMAIL = "ana@example.com";

    @Mock
    private BandMemberRepository bandMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BandAccessService accessService;

    @InjectMocks
    private BandMemberService bandMemberService;

    private User admin;
    private User ana;
    private Band band;
    private BandMember adminMembership;
    private BandMember anaMembership;

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

        adminMembership = new BandMember(band, admin, BandRole.ADMIN, "Baixo");
        anaMembership = new BandMember(band, ana, BandRole.MEMBER, "Voz");
    }

    @Test
    @DisplayName("O admin adiciona um membro com a função indicada")
    void addMember_Success() {
        BandMemberRequestDTO dto = new BandMemberRequestDTO();
        dto.setUserEmail(MEMBER_EMAIL);
        dto.setInstrument(" Voz ");

        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(userRepository.findByEmail(MEMBER_EMAIL)).thenReturn(Optional.of(ana));
        when(bandMemberRepository.save(any(BandMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BandMemberResponseDTO result = bandMemberService.addMemberToBand(5L, dto, ADMIN_EMAIL);

        assertThat(result.getRole()).isEqualTo(BandRole.MEMBER);
        assertThat(result.getInstrument()).isEqualTo("Voz");
        assertThat(result.getUserEmail()).isEqualTo(MEMBER_EMAIL);
    }

    @Test
    @DisplayName("Não deve adicionar um utilizador que já é membro")
    void addMember_AlreadyMember() {
        BandMemberRequestDTO dto = new BandMemberRequestDTO();
        dto.setUserEmail(MEMBER_EMAIL);

        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(userRepository.findByEmail(MEMBER_EMAIL)).thenReturn(Optional.of(ana));
        when(bandMemberRepository.existsByBandIdAndUserId(5L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> bandMemberService.addMemberToBand(5L, dto, ADMIN_EMAIL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O utilizador já é membro desta banda.");

        verify(bandMemberRepository, never()).save(any(BandMember.class));
    }

    @Test
    @DisplayName("O admin define a função de um membro")
    void updateMemberInstrument_Success() {
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(bandMemberRepository.findByBandIdAndUserId(5L, 2L)).thenReturn(Optional.of(anaMembership));
        when(bandMemberRepository.save(any(BandMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BandMemberResponseDTO result = bandMemberService.updateMemberInstrument(
                5L, 2L, new BandMemberUpdateDTO("Teclas"), ADMIN_EMAIL);

        assertThat(result.getInstrument()).isEqualTo("Teclas");
    }

    @Test
    @DisplayName("O admin remove um membro")
    void removeMember_Success() {
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(bandMemberRepository.findByBandIdAndUserId(5L, 2L)).thenReturn(Optional.of(anaMembership));

        bandMemberService.removeMemberFromBand(5L, 2L, ADMIN_EMAIL);

        verify(bandMemberRepository).delete(anaMembership);
    }

    @Test
    @DisplayName("O admin não pode ser removido da banda")
    void removeMember_AdminRefused() {
        when(accessService.requireAdmin(5L, ADMIN_EMAIL)).thenReturn(adminMembership);
        when(bandMemberRepository.findByBandIdAndUserId(5L, 1L)).thenReturn(Optional.of(adminMembership));

        assertThatThrownBy(() -> bandMemberService.removeMemberFromBand(5L, 1L, ADMIN_EMAIL))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bandMemberRepository, never()).delete(any(BandMember.class));
    }

    @Test
    @DisplayName("Um membro pode sair da banda")
    void leaveBand_MemberLeaves() {
        when(accessService.requireMembership(5L, MEMBER_EMAIL)).thenReturn(anaMembership);

        bandMemberService.leaveBand(5L, MEMBER_EMAIL);

        verify(bandMemberRepository).delete(anaMembership);
    }

    @Test
    @DisplayName("O admin não pode sair da banda")
    void leaveBand_AdminRefused() {
        when(accessService.requireMembership(5L, ADMIN_EMAIL)).thenReturn(adminMembership);

        assertThatThrownBy(() -> bandMemberService.leaveBand(5L, ADMIN_EMAIL))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bandMemberRepository, never()).delete(any(BandMember.class));
    }
}