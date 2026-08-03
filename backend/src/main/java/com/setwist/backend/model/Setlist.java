package com.setwist.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "setlists")
public class Setlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relação entre Song e Setlist
    @OneToMany(mappedBy = "setlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")

    private List<SetlistSong> setlistSongs = new ArrayList<>();

    // Construtor vazio
    public Setlist() {

    }

    // Construtor com parâmetros
    public Setlist(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Calculo dinâmico
    public Integer getTotalSongs() {
        return setlistSongs != null ? setlistSongs.size() : 0;
    }

    public Integer getTotalDurationSeconds() {
        if (setlistSongs == null || setlistSongs.isEmpty()) {
            return 0;
        }
        return setlistSongs.stream()
                .mapToInt(item -> (item.getSong() != null && item.getSong().getDurationSeconds() != null)
                        ? item.getSong().getDurationSeconds()
                        : 0)
                .sum();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<SetlistSong> getSetlistSongs() {
        return setlistSongs;
    }

    public void setSetlistSongs(List<SetlistSong> setlistSongs) {
        this.setlistSongs = setlistSongs;
    }
}
