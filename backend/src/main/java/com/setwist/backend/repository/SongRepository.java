package com.setwist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.setwist.backend.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {

}
