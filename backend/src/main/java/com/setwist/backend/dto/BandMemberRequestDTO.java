package com.setwist.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BandMemberRequestDTO {

    @NotBlank(message = "O e-mail do utilizador é obrigatório")
    @Email(message = "E-mail inválido")
    private String userEmail;

    @Size(max = 100, message = "A função não pode ter mais de 100 caracteres.")
    private String instrument;

    public BandMemberRequestDTO() {}

    public BandMemberRequestDTO(String userEmail, String instrument) {
        this.userEmail = userEmail;
        this.instrument = instrument;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }
}