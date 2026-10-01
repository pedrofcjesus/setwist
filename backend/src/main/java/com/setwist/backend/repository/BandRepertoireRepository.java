package com.setwist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.setwist.backend.model.BandRepertoire;

@Repository
public interface BandRepertoireRepository extends JpaRepository<BandRepertoire, Long> {
    
    List<BandRepertoire> findByBandId(Long bandId);
    
    boolean existsByBandIdAndSongId(Long bandId, Long songId);
}