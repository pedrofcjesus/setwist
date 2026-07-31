package com.setwist.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

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
    @ManyToMany
    @JoinTable(name = "setlist_songs", joinColumns = @JoinColumn(name = "setlist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))

    private List<Song> songs = new ArrayList<>();

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
        return songs != null ? songs.size() : 0;
    }

    public Integer getTotalDurationSeconds() {
        if (songs == null || songs.isEmpty()) {
            return 0;
        }
        return songs.stream()
                .mapToInt(song -> song.getDurationSeconds() != null ? song.getDurationSeconds() : 0)
                .sum();
    }

    // Getters e Setter
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

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
