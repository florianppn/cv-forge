package com.cvforge.dto.response;

import com.cvforge.domain.CvContent;
import com.cvforge.domain.CvProfile;
import com.cvforge.domain.CvStyle;

import java.time.Instant;
import java.util.UUID;

public class CvProfileResponse {

    private UUID id;
    private String sessionId;
    private String userEmail;
    private String targetCountry;
    private String photoUrl;
    private CvContent content;
    private CvStyle style;
    private Instant createdAt;
    private Instant updatedAt;

    public CvProfileResponse() {}

    public CvProfileResponse(UUID id, String sessionId, String userEmail, String targetCountry, String photoUrl, CvContent content, CvStyle style, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.userEmail = userEmail;
        this.targetCountry = targetCountry;
        this.photoUrl = photoUrl;
        this.content = content;
        this.style = style;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String sessionId;
        private String userEmail;
        private String targetCountry;
        private String photoUrl;
        private CvContent content;
        private CvStyle style;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder targetCountry(String targetCountry) { this.targetCountry = targetCountry; return this; }
        public Builder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public Builder content(CvContent content) { this.content = content; return this; }
        public Builder style(CvStyle style) { this.style = style; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public CvProfileResponse build() {
            return new CvProfileResponse(id, sessionId, userEmail, targetCountry, photoUrl, content, style, createdAt, updatedAt);
        }
    }

    public static CvProfileResponse fromEntity(CvProfile profile) {
        if (profile == null) {
            return null;
        }
        return CvProfileResponse.builder()
                .id(profile.getId())
                .sessionId(profile.getSessionId())
                .userEmail(profile.getUserEmail())
                .targetCountry(profile.getTargetCountry())
                .photoUrl(profile.getPhotoUrl())
                .content(profile.getContent())
                .style(profile.getStyle())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTargetCountry() { return targetCountry; }
    public void setTargetCountry(String targetCountry) { this.targetCountry = targetCountry; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public CvContent getContent() { return content; }
    public void setContent(CvContent content) { this.content = content; }

    public CvStyle getStyle() { return style; }
    public void setStyle(CvStyle style) { this.style = style; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
