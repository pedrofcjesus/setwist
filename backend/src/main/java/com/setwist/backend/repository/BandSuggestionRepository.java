package com.setwist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.setwist.backend.model.BandSuggestion;

public interface BandSuggestionRepository extends JpaRepository<BandSuggestion, Long> {

    @Modifying
    @Query("DELETE FROM BandSuggestion s WHERE s.song.id = :songId")
    void deleteBySongId(@Param("songId") Long songId);

    @Query("SELECT DISTINCT s.band.name FROM BandSuggestion s WHERE s.song.id = :songId")
    List<String> findBandNamesBySongId(@Param("songId") Long songId);
}