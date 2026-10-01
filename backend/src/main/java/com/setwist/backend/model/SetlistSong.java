package com.setwist.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "setlist_songs")
public class SetlistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "setlist_id")
    @JsonIgnore
    private Setlist setlist;

    // Aponta para o registo no repertório da banda
    @ManyToOne
    @JoinColumn(name = "band_repertoire_id")
    private BandRepertoire repertoireItem;

    private Integer position;

    private String notes; // Obs pontuais para a música neste concerto específico

    public SetlistSong() {}

    public SetlistSong(Setlist setlist, BandRepertoire repertoireItem, Integer position) {
        this.setlist = setlist;
        this.repertoireItem = repertoireItem;
        this.position = position;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Setlist getSetlist() { return setlist; }
    public void setSetlist(Setlist setlist) { this.setlist = setlist; }

    public BandRepertoire getRepertoireItem() { return repertoireItem; }
    public void setRepertoireItem(BandRepertoire repertoireItem) { this.repertoireItem = repertoireItem; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
