package com.setwist.backend.dto;

import com.setwist.backend.model.BandRepertoire;

public class BandRepertoireResponseDTO {

    private Long id;
    private Long songId;
    private String title;
    private String artist;
    private String songKey;
    private Integer durationSeconds;
    private Integer bpm;
    private String notes;

    public BandRepertoireResponseDTO() {
    }

    public BandRepertoireResponseDTO(BandRepertoire repertoire) {
        this.id = repertoire.getId();
        if (repertoire.getSong() != null) {
            this.songId = repertoire.getSong().getId();
            this.title = repertoire.getSong().getTitle();
            this.artist = repertoire.getSong().getArtist();
        }
        this.songKey = repertoire.getSongKey();
        this.durationSeconds = repertoire.getDurationSeconds();
        this.bpm = repertoire.getBpm();
        this.notes = repertoire.getNotes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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