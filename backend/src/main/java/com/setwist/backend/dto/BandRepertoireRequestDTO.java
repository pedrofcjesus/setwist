package com.setwist.backend.dto;

import jakarta.validation.constraints.NotNull;

public class BandRepertoireRequestDTO {

    @NotNull(message = "O ID da música da biblioteca é obrigatório")
    private Long songId;

    private String songKey;
    private Integer durationSeconds;
    private Integer bpm;
    private String notes;

    public BandRepertoireRequestDTO() {
    }

    public BandRepertoireRequestDTO(Long songId, String songKey, Integer durationSeconds, Integer bpm, String notes) {
        this.songId = songId;
        this.songKey = songKey;
        this.durationSeconds = durationSeconds;
        this.bpm = bpm;
        this.notes = notes;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getSongKey() {
        return songKey;
    }

    public void setSongKey(String songKey) {
        this.songKey = songKey;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}