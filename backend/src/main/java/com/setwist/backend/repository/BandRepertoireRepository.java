package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.BandRepertoire;

public interface BandRepertoireRepository extends JpaRepository<BandRepertoire, Long> {

    List<BandRepertoire> findByBandId(Long bandId);

    Optional<BandRepertoire> findByIdAndBandId(Long id, Long bandId);
}