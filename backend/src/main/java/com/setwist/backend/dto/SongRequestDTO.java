package com.setwist.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SongRequestDTO {

    @NotBlank(message = "O título da música é obrigatório.")
    @Size(max = 50, message = "O título não pode ter mais de 50 caracteres.")
    private String title;

    @NotBlank(message = "O nome do artista é obrigatório.")
    @Size(max = 50, message = "O nome do artista não pode ter mais de 50 caracteres.")
    private String artist;

    @Size(max = 10, message = "A tonalidade não pode ter mais de 10 caracteres.")
    private String songKey;

    @NotNull(message = "A duração é obrigatória.")
    @Min(value = 1, message = "A duração tem de ser pelo menos 1 segundo.")
    private Integer durationSeconds;

    // Construtor vazio
    public SongRequestDTO() {
    }

    public SongRequestDTO(String title, String artist, String songKey, Integer durationSeconds) {
        this.title = title;
        this.artist = artist;
        this.songKey = songKey;
        this.durationSeconds = durationSeconds;
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
