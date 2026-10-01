package com.setwist.backend.dto;

public class SongSummaryDTO {

    private Long id;
    private String title;
    private String artist;

    public SongSummaryDTO() {
    }

    public SongSummaryDTO(Long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
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

}
