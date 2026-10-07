package com.setwist.backend.dto;

import java.util.List;

import com.setwist.backend.model.Song;

public class SongResponseDTO {
    private Long id;
    private String title;
    private String artist;
    private List<String> bands;
    private List<String> suggestedInBands;

    public SongResponseDTO() {
    }

    public SongResponseDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
    }

    public SongResponseDTO(Song song, List<String> bands) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.bands = bands;
    }

    public SongResponseDTO(Song song, List<String> bands, List<String> suggestedInBands) {
        this(song, bands);
        this.suggestedInBands = suggestedInBands;
    }

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

    public List<String> getBands() {
        return bands;
    }

    public void setBands(List<String> bands) {
        this.bands = bands;
    }

    public List<String> getSuggestedInBands() {
        return suggestedInBands;
    }

    public void setSuggestedInBands(List<String> suggestedInBands) {
        this.suggestedInBands = suggestedInBands;
    }
}
