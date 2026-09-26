package com.cvforge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cv_profiles")
public class CvProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "target_country", length = 10)
    private String targetCountry;

    @Column(name = "photo_url")
    private String photoUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", columnDefinition = "jsonb")
    private CvContent content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "style", columnDefinition = "jsonb")
    private CvStyle style;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CvProfile() {}

    public CvProfile(UUID id, String sessionId, String userEmail, String targetCountry, String photoUrl, CvContent content, CvStyle style, Instant createdAt, Instant updatedAt) {
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
        public CvProfile build() {
            return new CvProfile(id, sessionId, userEmail, targetCountry, photoUrl, content, style, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.style == null) {
            this.style = CvStyle.defaultStyle();
        }
        applyCountryAtsRules();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        applyCountryAtsRules();
    }

    public void applyCountryAtsRules() {
        if (targetCountry != null && isAngloSaxonCountry(targetCountry)) {
            if (this.style != null) {
                this.style.setShowPhoto(false);
            }
        }
    }

    private boolean isAngloSaxonCountry(String country) {
        String c = country.trim().toUpperCase();
        return c.equals("US") || c.equals("USA") || c.equals("UK") || c.equals("GB") || c.equals("CA") || c.equals("AU");
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
