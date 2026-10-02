package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.setwist.backend.model.SetlistSong;

public interface SetlistSongRepository extends JpaRepository<SetlistSong, Long> {

    List<SetlistSong> findBySetlistIdOrderByPositionAsc(Long setlistId);

    Optional<SetlistSong> findBySetlistIdAndRepertoireItemId(Long setlistId, Long repertoireItemId);

    void deleteBySetlistIdAndRepertoireItemId(Long setlistId, Long repertoireItemId);

    @Modifying
    @Query("DELETE FROM SetlistSong s WHERE s.repertoireItem.song.id = :songId")
    void deleteBySongId(@Param("songId") Long songId);
}