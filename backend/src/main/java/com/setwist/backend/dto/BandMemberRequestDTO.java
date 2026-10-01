package com.setwist.backend.dto;

import com.setwist.backend.model.BandRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BandMemberRequestDTO {

    @NotBlank(message = "O e-mail do utilizador é obrigatório")
    @Email(message = "E-mail inválido")
    private String userEmail;

    @NotNull(message = "A permissão (role) é obrigatória")
    private BandRole role;

    public BandMemberRequestDTO() {}

    public BandMemberRequestDTO(String userEmail, BandRole role) {
        this.userEmail = userEmail;
        this.role = role;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public BandRole getRole() { return role; }
    public void setRole(BandRole role) { this.role = role; }
}