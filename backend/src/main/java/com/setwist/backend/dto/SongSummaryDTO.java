package com.setwist.backend.dto;

public class SongSummaryDTO {

    private Long id;
    private String title;
    private String artist;
    private String songKey;
    private Integer durationSeconds;

    public SongSummaryDTO() {
    }

    public SongSummaryDTO(Long id, String title, String artist, String songKey, Integer durationSeconds) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.songKey = songKey;
        this.durationSeconds = durationSeconds;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
