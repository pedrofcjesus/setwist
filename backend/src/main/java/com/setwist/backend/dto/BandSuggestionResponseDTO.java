package com.setwist.backend.dto;

import java.time.LocalDateTime;

import com.setwist.backend.model.BandSuggestion;

public class BandSuggestionResponseDTO {

    private Long id;            // id da sugestão
    private Long songId;        // id da música (usado nos endpoints de retirar/promover)
    private String title;
    private String artist;
    private Long addedByUserId;
    private String addedByName;
    private LocalDateTime createdAt;
    private boolean canEdit;    // o utilizador atual pode editar a música (é o autor)
    private boolean canRemove;  // o utilizador atual pode retirar a sugestão (autor ou admin)

    public BandSuggestionResponseDTO() {
    }

    public BandSuggestionResponseDTO(BandSuggestion suggestion, boolean canEdit, boolean canRemove) {
        this.id = suggestion.getId();
        if (suggestion.getSong() != null) {
            this.songId = suggestion.getSong().getId();
            this.title = suggestion.getSong().getTitle();
            this.artist = suggestion.getSong().getArtist();
        }
        if (suggestion.getAddedBy() != null) {
            this.addedByUserId = suggestion.getAddedBy().getId();
            this.addedByName = suggestion.getAddedBy().getName();
        }
        this.createdAt = suggestion.getCreatedAt();
        this.canEdit = canEdit;
        this.canRemove = canRemove;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public Long getAddedByUserId() { return addedByUserId; }
    public void setAddedByUserId(Long addedByUserId) { this.addedByUserId = addedByUserId; }

    public String getAddedByName() { return addedByName; }
    public void setAddedByName(String addedByName) { this.addedByName = addedByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }

    public boolean isCanRemove() { return canRemove; }
    public void setCanRemove(boolean canRemove) { this.canRemove = canRemove; }
}