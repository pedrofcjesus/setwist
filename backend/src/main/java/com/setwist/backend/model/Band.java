package com.setwist.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "bands")
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Criador/Dono da Banda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Membros e respetivas permissões
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandMember> members = new ArrayList<>();

    // Repertório ativo da banda
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BandRepertoire> repertoire = new ArrayList<>();

    // Sugestões de músicas para a banda (com autor)
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @JsonIgnore
    private List<BandSuggestion> suggestions = new ArrayList<>();

    // Setlists da banda
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Setlist> setlists = new ArrayList<>();

    public Band() {}

    public Band(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<BandMember> getMembers() { return members; }
    public void setMembers(List<BandMember> members) { this.members = members; }

    public List<BandRepertoire> getRepertoire() { return repertoire; }
    public void setRepertoire(List<BandRepertoire> repertoire) { this.repertoire = repertoire; }

    public List<BandSuggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<BandSuggestion> suggestions) { this.suggestions = suggestions; }

    public List<Setlist> getSetlists() { return setlists; }
    public void setSetlists(List<Setlist> setlists) { this.setlists = setlists; }
}