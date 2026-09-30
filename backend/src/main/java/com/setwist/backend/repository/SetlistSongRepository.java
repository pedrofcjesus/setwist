package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.SetlistSong;

public interface SetlistSongRepository extends JpaRepository<SetlistSong, Long> {

    List<SetlistSong> findBySetlistIdOrderByPositionAsc(Long setlistId);

    Optional<SetlistSong> findBySetlistIdAndSongId(Long setlistId, Long songId);

    void deleteBySetlistIdAndSongId(Long setlistId, Long songId);
}
