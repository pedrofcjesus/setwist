package com.setwist.backend.dto;

import com.setwist.backend.model.Song;

public class SongResponseDTO {
    private Long id;
    private String title;
    private String artist;
    private String songKey;
    private Integer durationSeconds;
    private BandSummaryDTO band;

    public SongResponseDTO() {
    }

    public SongResponseDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.songKey = song.getSongKey();
        this.durationSeconds = song.getDurationSeconds();

        if (song.getBand() != null) {
            this.band = new BandSummaryDTO(song.getBand().getId(), song.getBand().getName());
        }
    }

    // Getters e Setters
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

    public BandSummaryDTO getBand() {
        return band;
    }

    public void setBand(BandSummaryDTO band) {
        this.band = band;
    }
}
