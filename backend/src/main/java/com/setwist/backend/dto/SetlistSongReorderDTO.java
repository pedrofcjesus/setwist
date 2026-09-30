package com.setwist.backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class SetlistSongReorderDTO {

    @NotEmpty(message = "A lista de IDs de músicas não pode estar vazia")
    private List<Long> songIds;

    public SetlistSongReorderDTO() {
    }

    public SetlistSongReorderDTO(List<Long> songIds) {
        this.songIds = songIds;
    }

    public List<Long> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }
}
