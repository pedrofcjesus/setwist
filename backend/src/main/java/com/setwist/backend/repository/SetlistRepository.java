package com.setwist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.Setlist;

public interface SetlistRepository extends JpaRepository<Setlist, Long> {
    List<Setlist> findByBandId(Long bandId);
}
