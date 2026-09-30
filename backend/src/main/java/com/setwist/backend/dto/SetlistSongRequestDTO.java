package com.setwist.backend.dto;

import jakarta.validation.constraints.NotNull;

public class SetlistSongRequestDTO {

    @NotNull(message = "O ID da música é obrigatório")
    private Long songId;

    private Integer position; // Opcional: se não for enviado, vai para o fim da lista

    public SetlistSongRequestDTO() {
    }

    public SetlistSongRequestDTO(Long songId, Integer position) {
        this.songId = songId;
        this.position = position;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
