package com.setwist.backend.dto;

import com.setwist.backend.model.SetlistSong;

public class SetlistSongResponseDTO {

    private Long id;
    private Integer position;
    private SongSummaryDTO song;

    public SetlistSongResponseDTO() {
    }

    public SetlistSongResponseDTO(SetlistSong setlistSong) {
        this.id = setlistSong.getId();
        this.position = setlistSong.getPosition();
        if (setlistSong.getSong() != null) {
            this.song = new SongSummaryDTO(
                    setlistSong.getSong().getId(),
                    setlistSong.getSong().getTitle(),
                    setlistSong.getSong().getArtist(),
                    setlistSong.getSong().getSongKey(),
                    setlistSong.getSong().getDurationSeconds());
        }
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public SongSummaryDTO getSong() {
        return song;
    }

    public void setSong(SongSummaryDTO song) {
        this.song = song;
    }
}
