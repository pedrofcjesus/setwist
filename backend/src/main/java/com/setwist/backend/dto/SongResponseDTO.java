package com.setwist.backend.dto;

import com.setwist.backend.model.Song;

public class SongResponseDTO {
    private Long id;
    private String title;
    private String artist;

    public SongResponseDTO() {
    }

    public SongResponseDTO(Song song) {
        this.id = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
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
}
