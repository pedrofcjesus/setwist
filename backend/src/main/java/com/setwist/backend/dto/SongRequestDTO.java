package com.setwist.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class SongRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    private String artist;

    private Integer durationSeconds;

    private String songKey;

    private Long bandId; // Campo para associar a música a uma banda

    public SongRequestDTO() {
    }

    public SongRequestDTO(String title, String artist, Integer durationSeconds, String songKey, Long bandId) {
        this.title = title;
        this.artist = artist;
        this.durationSeconds = durationSeconds;
        this.songKey = songKey;
        this.bandId = bandId;
    }

    // Getters e Setters
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

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getSongKey() {
        return songKey;
    }

    public void setSongKey(String songKey) {
        this.songKey = songKey;
    }

    public Long getBandId() {
        return bandId;
    }

    public void setBandId(Long bandId) {
        this.bandId = bandId;
    }
}
