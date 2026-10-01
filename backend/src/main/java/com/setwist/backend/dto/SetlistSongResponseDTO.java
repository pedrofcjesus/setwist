package com.setwist.backend.dto;

import com.setwist.backend.model.SetlistSong;

public class SetlistSongResponseDTO {

    private Long id;
    private Integer position;
    private String notes;
    private BandRepertoireResponseDTO repertoireItem;

    public SetlistSongResponseDTO() {
    }

    public SetlistSongResponseDTO(SetlistSong setlistSong) {
        this.id = setlistSong.getId();
        this.position = setlistSong.getPosition();
        this.notes = setlistSong.getNotes();
        if (setlistSong.getRepertoireItem() != null) {
            this.repertoireItem = new BandRepertoireResponseDTO(setlistSong.getRepertoireItem());
        }
    }

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BandRepertoireResponseDTO getRepertoireItem() {
        return repertoireItem;
    }

    public void setRepertoireItem(BandRepertoireResponseDTO repertoireItem) {
        this.repertoireItem = repertoireItem;
    }
}