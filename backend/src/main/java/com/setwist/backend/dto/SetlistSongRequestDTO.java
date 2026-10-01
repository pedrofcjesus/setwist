package com.setwist.backend.dto;

import jakarta.validation.constraints.NotNull;

public class SetlistSongRequestDTO {

    @NotNull(message = "O ID do item de repertório da banda é obrigatório")
    private Long repertoireItemId;

    private Integer position;
    private String notes;

    public SetlistSongRequestDTO() {
    }

    public SetlistSongRequestDTO(Long repertoireItemId, Integer position, String notes) {
        this.repertoireItemId = repertoireItemId;
        this.position = position;
        this.notes = notes;
    }

    public Long getRepertoireItemId() {
        return repertoireItemId;
    }

    public void setRepertoireItemId(Long repertoireItemId) {
        this.repertoireItemId = repertoireItemId;
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
}