package com.setwist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.setwist.backend.model.Band;

public interface BandRepository extends JpaRepository<Band, Long> {

    // Bandas cujo criador ainda não tem linha em band_members (preenchimento inicial)
    @Query("""
            SELECT b FROM Band b JOIN FETCH b.user
            WHERE NOT EXISTS (
                SELECT 1 FROM BandMember m WHERE m.band = b AND m.user = b.user
            )
            """)
    List<Band> findBandsWithoutOwnerMembership();
}
