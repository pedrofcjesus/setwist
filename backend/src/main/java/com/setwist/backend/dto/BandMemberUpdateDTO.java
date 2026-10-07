package com.setwist.backend.dto;

import jakarta.validation.constraints.Size;

public class BandMemberUpdateDTO {

    @Size(max = 100, message = "A função não pode ter mais de 100 caracteres.")
    private String instrument;

    public BandMemberUpdateDTO() {}

    public BandMemberUpdateDTO(String instrument) {
        this.instrument = instrument;
    }

    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }
}