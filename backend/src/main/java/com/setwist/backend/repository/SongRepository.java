package com.setwist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByBandId(Long bandId);

}
