package com.setwist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.setwist.backend.model.Setlist;

public interface SetlistRepository extends JpaRepository<Setlist, Long> {

    // Setlists das bandas de que o utilizador é membro + setlists pessoais (sem banda) do próprio
    @Query("""
            SELECT s FROM Setlist s
            WHERE (s.band IS NULL AND s.user.email = :email)
            OR s.band.id IN (SELECT m.band.id FROM BandMember m WHERE m.user.email = :email)
            ORDER BY s.id
            """)
    List<Setlist> findAccessibleByUserEmail(@Param("email") String email);
}