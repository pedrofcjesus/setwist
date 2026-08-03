package com.setwist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setwist.backend.model.Band;

public interface BandRepository extends JpaRepository<Band, Long> {

}
