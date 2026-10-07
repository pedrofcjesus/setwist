package com.setwist.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "band_members")
public class BandMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    @JsonIgnore
    private Band band;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Permissão dentro da banda
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BandRole role = BandRole.MEMBER;

    // Função na banda (texto livre): "Baixo", "Voz", "Técnico de som", ...
    @Column(length = 100)
    private String instrument;

    public BandMember() {}

    public BandMember(Band band, User user, BandRole role) {
        this.band = band;
        this.user = user;
        this.role = role;
    }

    public BandMember(Band band, User user, BandRole role, String instrument) {
        this(band, user, role);
        this.instrument = instrument;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Band getBand() { return band; }
    public void setBand(Band band) { this.band = band; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BandRole getRole() { return role; }
    public void setRole(BandRole role) { this.role = role; }

    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }
}