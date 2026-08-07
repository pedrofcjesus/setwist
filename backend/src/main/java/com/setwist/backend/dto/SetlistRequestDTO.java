package com.setwist.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SetlistRequestDTO {

    @NotBlank(message = "O nome da setlist é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome da setlist deve ter entre 2 e 100 caracteres.")
    private String name;

    @Size(max = 500, message = "A descrição não pode ter mais de 500 caracteres.")
    private String description;

    public SetlistRequestDTO() {
    }

    public SetlistRequestDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

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