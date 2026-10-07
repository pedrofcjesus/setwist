package com.setwist.backend.dto;

import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRole;

public class BandMemberResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private BandRole role;
    private String instrument;
    private boolean currentUser;

    public BandMemberResponseDTO() {}

    public BandMemberResponseDTO(BandMember member) {
        this.id = member.getId();
        if (member.getUser() != null) {
            this.userId = member.getUser().getId();
            this.userName = member.getUser().getName();
            this.userEmail = member.getUser().getEmail();
        }
        this.role = member.getRole();
        this.instrument = member.getInstrument();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public BandRole getRole() { return role; }
    public void setRole(BandRole role) { this.role = role; }

    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }

    public boolean isCurrentUser() { return currentUser; }
    public void setCurrentUser(boolean currentUser) { this.currentUser = currentUser; }
}