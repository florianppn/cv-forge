package com.cvforge.dto.request;

import com.cvforge.domain.CvContent;
import jakarta.validation.constraints.NotNull;

public class CvContentUpdateRequest {

    @NotNull(message = "Le contenu du CV ne peut pas être null")
    private CvContent content;

    public CvContentUpdateRequest() {}

    public CvContentUpdateRequest(CvContent content) {
        this.content = content;
    }

    public CvContent getContent() { return content; }
    public void setContent(CvContent content) { this.content = content; }
}
