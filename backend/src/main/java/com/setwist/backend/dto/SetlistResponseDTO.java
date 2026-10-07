package com.setwist.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.setwist.backend.model.Setlist;

public class SetlistResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BandSummaryDTO band;
    private List<SetlistSongResponseDTO> setlistSongs = new ArrayList<>();
    private Integer totalSongs;
    private Integer totalDurationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canEdit;

    public SetlistResponseDTO() {
    }

    public SetlistResponseDTO(Setlist setlist) {
        this.id = setlist.getId();
        this.name = setlist.getName();
        this.description = setlist.getDescription();
        this.createdAt = setlist.getCreatedAt();
        this.updatedAt = setlist.getUpdatedAt();

        if (setlist.getBand() != null) {
            this.band = new BandSummaryDTO(setlist.getBand().getId(),
                    setlist.getBand().getName());
        }

        if (setlist.getSetlistSongs() != null) {
            this.setlistSongs = setlist.getSetlistSongs()
                    .stream()
                    .map(SetlistSongResponseDTO::new)
                    .toList();
        }

        this.totalSongs = setlist.getTotalSongs();
        this.totalDurationSeconds = setlist.getTotalDurationSeconds();
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

    public BandSummaryDTO getBand() {
        return band;
    }

    public void setBand(BandSummaryDTO band) {
        this.band = band;
    }

    public List<SetlistSongResponseDTO> getSetlistSongs() {
        return setlistSongs;
    }

    public void setSetlistSongs(List<SetlistSongResponseDTO> setlistSongs) {
        this.setlistSongs = setlistSongs;
    }

    public Integer getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(Integer totalSongs) {
        this.totalSongs = totalSongs;
    }

    public Integer getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(Integer totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
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

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
