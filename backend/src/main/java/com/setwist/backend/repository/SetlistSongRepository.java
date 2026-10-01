package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.SetlistSong;

public interface SetlistSongRepository extends JpaRepository<SetlistSong, Long> {

    List<SetlistSong> findBySetlistIdOrderByPositionAsc(Long setlistId);

    Optional<SetlistSong> findBySetlistIdAndRepertoireItemId(Long setlistId, Long repertoireItemId);

    void deleteBySetlistIdAndRepertoireItemId(Long setlistId, Long repertoireItemId);
}
