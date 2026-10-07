package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.BandMember;

public interface BandMemberRepository extends JpaRepository<BandMember, Long> {

    @EntityGraph(attributePaths = "user")
    List<BandMember> findByBandId(Long bandId);

    @EntityGraph(attributePaths = "band")
    List<BandMember> findByUserEmail(String email);

    List<BandMember> findByUserId(Long userId);

    Optional<BandMember> findByBandIdAndUserId(Long bandId, Long userId);

    Optional<BandMember> findByBandIdAndUserEmail(Long bandId, String email);

    boolean existsByBandIdAndUserId(Long bandId, Long userId);
}