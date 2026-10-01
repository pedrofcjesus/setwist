package com.setwist.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class SongRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    private String artist;

    public SongRequestDTO() {
    }

    public SongRequestDTO(String title, String artist) {
        this.title = title;
        this.artist = artist;
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
