package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.setwist.backend.model.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByUserEmail(String email);
    Optional<Song> findByIdAndUserEmail(Long id, String email);
}
