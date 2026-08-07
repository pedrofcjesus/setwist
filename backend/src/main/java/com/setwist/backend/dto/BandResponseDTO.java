package com.setwist.backend.dto;

import java.time.LocalDateTime;

import com.setwist.backend.model.Band;

public class BandResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BandResponseDTO() {
    }

    public BandResponseDTO(Band band) {
        this.id = band.getId();
        this.name = band.getName();
        this.description = band.getDescription();
        this.createdAt = band.getCreatedAt();
        this.updatedAt = band.getUpdatedAt();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
