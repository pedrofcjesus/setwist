package com.setwist.backend.dto;

import java.time.LocalDateTime;

import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandRole;

public class BandResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BandRole currentUserRole;
    private String currentUserInstrument;

    public BandResponseDTO() {
    }

    public BandResponseDTO(Band band) {
        this.id = band.getId();
        this.name = band.getName();
        this.description = band.getDescription();
        this.createdAt = band.getCreatedAt();
        this.updatedAt = band.getUpdatedAt();
    }

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

    public BandRole getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(BandRole currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public String getCurrentUserInstrument() {
        return currentUserInstrument;
    }

    public void setCurrentUserInstrument(String currentUserInstrument) {
        this.currentUserInstrument = currentUserInstrument;
    }
}
