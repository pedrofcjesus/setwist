package com.setwist.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.setwist.backend.model.Band;

@Repository
public interface BandRepository extends JpaRepository<Band, Long> {
    List<Band> findByUserEmail(String email);
    Optional<Band> findByIdAndUserEmail(Long id, String email);
}
