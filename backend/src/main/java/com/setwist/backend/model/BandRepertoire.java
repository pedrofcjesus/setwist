package com.setwist.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "band_repertoire")
public class BandRepertoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Corrigido: aponta para band_id e usa FetchType.LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    @JsonIgnore
    private Band band;

    // Mantém: aponta para song_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "song_key")
    private String songKey;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    private Integer bpm;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public BandRepertoire() {}

    public BandRepertoire(Band band, Song song, String songKey, Integer durationSeconds, Integer bpm) {
        this.band = band;
        this.song = song;
        this.songKey = songKey;
        this.durationSeconds = durationSeconds;
        this.bpm = bpm;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Band getBand() { return band; }
    public void setBand(Band band) { this.band = band; }

    public Song getSong() { return song; }
    public void setSong(Song song) { this.song = song; }

    public String getSongKey() { return songKey; }
    public void setSongKey(String songKey) { this.songKey = songKey; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Integer getBpm() { return bpm; }
    public void setBpm(Integer bpm) { this.bpm = bpm; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}