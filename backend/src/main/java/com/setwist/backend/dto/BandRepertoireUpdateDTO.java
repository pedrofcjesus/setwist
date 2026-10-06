package com.setwist.backend.dto;

public class BandRepertoireUpdateDTO {

    private String songKey;
    private Integer durationSeconds;
    private Integer bpm;
    private String notes;

    public BandRepertoireUpdateDTO() {
    }

    public BandRepertoireUpdateDTO(String songKey, Integer durationSeconds, Integer bpm, String notes) {
        this.songKey = songKey;
        this.durationSeconds = durationSeconds;
        this.bpm = bpm;
        this.notes = notes;
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