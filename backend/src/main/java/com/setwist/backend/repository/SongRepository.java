package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByUserEmail(String userEmail);

    Optional<Song> findByIdAndUserEmail(Long id, String userEmail);
}