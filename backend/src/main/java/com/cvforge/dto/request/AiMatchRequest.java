package com.cvforge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiMatchRequest {

    @NotBlank(message = "Le texte de l'offre d'emploi est obligatoire pour l'analyse de matching")
    @Size(max = 10000, message = "Le texte de l'offre ne doit pas dépasser 10 000 caractères")
    private String jobOfferText;

    public AiMatchRequest() {}

    public AiMatchRequest(String jobOfferText) {
        this.jobOfferText = jobOfferText;
    }

    public String getJobOfferText() { return jobOfferText; }
    public void setJobOfferText(String jobOfferText) { this.jobOfferText = jobOfferText; }
}
