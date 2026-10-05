package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.setwist.backend.model.SetlistSong;

public interface SetlistSongRepository extends JpaRepository<SetlistSong, Long> {

    List<SetlistSong> findBySetlistIdOrderByPositionAsc(Long setlistId);

    Optional<SetlistSong> findBySetlistIdAndRepertoireItemId(Long setlistId, Long repertoireItemId);

    void deleteBySetlistIdAndRepertoireItemId(Long setlistId, Long repertoireItemId);

    @Modifying
    @Query("DELETE FROM SetlistSong s WHERE s.repertoireItem.song.id = :songId")
    void deleteBySongId(@Param("songId") Long songId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SetlistSong s WHERE s.repertoireItem.id = :repertoireItemId")
    void deleteByRepertoireItemId(@Param("repertoireItemId") Long repertoireItemId);
}