package com.cvforge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiCvGenerateRequest {

    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String portfolioUrl;

    @NotBlank(message = "Le poste visé est obligatoire")
    private String targetJobTitle;

    @NotBlank(message = "La description brute de votre parcours est obligatoire")
    @Size(max = 10000, message = "Le texte du profil ne doit pas dépasser 10 000 caractères")
    private String rawProfileText;

    @Size(max = 10000, message = "Le texte de l'offre d'emploi ne doit pas dépasser 10 000 caractères")
    private String rawJobOfferText;

    private String targetCountry = "FR";
    private String sessionId;

    public AiCvGenerateRequest() {}

    public AiCvGenerateRequest(String fullName, String email, String phone, String location, String linkedinUrl, String portfolioUrl, String targetJobTitle, String rawProfileText, String rawJobOfferText, String targetCountry, String sessionId) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.linkedinUrl = linkedinUrl;
        this.portfolioUrl = portfolioUrl;
        this.targetJobTitle = targetJobTitle;
        this.rawProfileText = rawProfileText;
        this.rawJobOfferText = rawJobOfferText;
        this.targetCountry = targetCountry != null ? targetCountry : "FR";
        this.sessionId = sessionId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fullName;
        private String email;
        private String phone;
        private String location;
        private String linkedinUrl;
        private String portfolioUrl;
        private String targetJobTitle;
        private String rawProfileText;
        private String rawJobOfferText;
        private String targetCountry = "FR";
        private String sessionId;

        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder linkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; return this; }
        public Builder portfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; return this; }
        public Builder targetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; return this; }
        public Builder rawProfileText(String rawProfileText) { this.rawProfileText = rawProfileText; return this; }
        public Builder rawJobOfferText(String rawJobOfferText) { this.rawJobOfferText = rawJobOfferText; return this; }
        public Builder targetCountry(String targetCountry) { this.targetCountry = targetCountry; return this; }
        public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public AiCvGenerateRequest build() {
            return new AiCvGenerateRequest(fullName, email, phone, location, linkedinUrl, portfolioUrl, targetJobTitle, rawProfileText, rawJobOfferText, targetCountry, sessionId);
        }
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public String getTargetJobTitle() { return targetJobTitle; }
    public void setTargetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; }

    public String getRawProfileText() { return rawProfileText; }
    public void setRawProfileText(String rawProfileText) { this.rawProfileText = rawProfileText; }

    public String getRawJobOfferText() { return rawJobOfferText; }
    public void setRawJobOfferText(String rawJobOfferText) { this.rawJobOfferText = rawJobOfferText; }

    public String getTargetCountry() { return targetCountry; }
    public void setTargetCountry(String targetCountry) { this.targetCountry = targetCountry; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
