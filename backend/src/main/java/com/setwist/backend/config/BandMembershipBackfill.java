package com.setwist.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepository;

/**
 * Garante que o criador de cada banda existente é ADMIN e membro dela.
 * Idempotente: só atua em bandas cujo criador ainda não tem linha em band_members.
 */
@Configuration
public class BandMembershipBackfill {

    @Bean
    CommandLineRunner backfillBandMemberships(BandRepository bandRepository, BandMemberRepository bandMemberRepository) {
        return args -> {
            List<Band> bands = bandRepository.findBandsWithoutOwnerMembership();
            if (bands.isEmpty()) {
                return;
            }

            List<BandMember> memberships = bands.stream()
                    .map(b -> new BandMember(b, b.getUser(), BandRole.ADMIN))
                    .toList();
            bandMemberRepository.saveAll(memberships);

            System.out.println(">>> [SETWIST] Criador definido como ADMIN em " + memberships.size() + " banda(s) existente(s). <<<");
        };
    }
}