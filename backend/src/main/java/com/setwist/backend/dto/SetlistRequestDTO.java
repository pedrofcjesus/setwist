package com.setwist.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class SetlistRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    private String description;

    private Long bandId; // Campo para associar a setlist a uma banda

    public SetlistRequestDTO() {
    }

    public SetlistRequestDTO(String name, String description, Long bandId) {
        this.name = name;
        this.description = description;
        this.bandId = bandId;
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBandId() {
        return bandId;
    }

    public void setBandId(Long bandId) {
        this.bandId = bandId;
    }
}