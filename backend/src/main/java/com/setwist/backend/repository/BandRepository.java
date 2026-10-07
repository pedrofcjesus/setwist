package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.setwist.backend.model.Band;

public interface BandRepository extends JpaRepository<Band, Long> {
    List<Band> findByUserEmail(String email);
    Optional<Band> findByIdAndUserEmail(Long id, String email);

    @Query("SELECT DISTINCT b.name FROM Band b JOIN b.suggestions s WHERE s.id = :songId")
    List<String> findBandNamesBySuggestedSongId(@Param("songId") Long songId);

    @Modifying
    @Query(value = "DELETE FROM band_suggestions WHERE song_id = :songId", nativeQuery = true)
    void deleteSuggestionsBySongId(@Param("songId") Long songId);
}
