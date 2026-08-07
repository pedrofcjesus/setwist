package com.setwist.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BandRequestDTO {

    @NotBlank(message = "O nome da banda é obrigatório.")
    @Size(min = 2, max = 50, message = "O nome da banda deve ter entre 2 e 50 caracteres.")
    private String name;

    @Size(max = 500, message = "A descrição não pode ter mais de 500 caracteres.")
    private String description;

    // Construtor vazio
    public BandRequestDTO() {
    }

    public BandRequestDTO(String name, String description) {
        this.name = name;
        this.description = description;
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
}