package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.BandMember;


public interface BandMemberRepository extends JpaRepository<BandMember, Long> {

    List<BandMember> findByBandId(Long bandId);

    List<BandMember> findByUserId(Long userId);

    Optional<BandMember> findByBandIdAndUserId(Long bandId, Long userId);

    boolean existsByBandIdAndUserId(Long bandId, Long userId);
}