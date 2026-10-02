package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.setwist.backend.model.BandRepertoire;

public interface BandRepertoireRepository extends JpaRepository<BandRepertoire, Long> {

    List<BandRepertoire> findByBandId(Long bandId);

    Optional<BandRepertoire> findByIdAndBandId(Long id, Long bandId);

    void deleteBySongId(Long songId);

    @Query("SELECT DISTINCT r.band.name FROM BandRepertoire r WHERE r.song.id = :songId")
    List<String> findBandNamesBySongId(@Param("songId") Long songId);
}