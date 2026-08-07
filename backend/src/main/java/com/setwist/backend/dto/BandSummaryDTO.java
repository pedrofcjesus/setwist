package com.setwist.backend.dto;

import com.setwist.backend.model.Band;

public class BandSummaryDTO {

    private Long id;
    private String name;

    public BandSummaryDTO() {
    }

    public BandSummaryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public BandSummaryDTO(Band band) {
        if (band != null) {
            this.id = band.getId();
            this.name = band.getName();
        }
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}