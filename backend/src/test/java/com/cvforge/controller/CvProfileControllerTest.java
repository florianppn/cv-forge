package com.cvforge.controller;

import com.cvforge.domain.CvContent;
import com.cvforge.domain.CvProfile;
import com.cvforge.domain.CvStyle;
import com.cvforge.dto.request.AiCvGenerateRequest;
import com.cvforge.dto.request.AiMatchRequest;
import com.cvforge.dto.request.CvContentUpdateRequest;
import com.cvforge.dto.request.CvStyleUpdateRequest;
import com.cvforge.repository.CvProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CvProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CvProfileRepository cvProfileRepository;

    @Test
    @DisplayName("POST /api/v1/cv/generate - Génération nominale d'un CV")
    void testGenerateCvEndpoint() throws Exception {
        AiCvGenerateRequest request = AiCvGenerateRequest.builder()
                .fullName("Florian Pépin")
                .email("florian@example.com")
                .targetJobTitle("Architecte Java")
                .rawProfileText("Expertise en microservices et Spring AI")
                .targetCountry("FR")
                .build();

        mockMvc.perform(post("/api/v1/cv/generate")
                        .header("X-Session-Id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.content.contactInfo.fullName", is("Florian Pépin")))
                .andExpect(jsonPath("$.style.primaryColor", is("#1a365d")));
    }

    @Test
    @DisplayName("POST /api/v1/cv/generate - Validation 400 si champs obligatoires absents")
    void testGenerateCvEndpoint_ValidationFailure() throws Exception {
        AiCvGenerateRequest invalidReq = new AiCvGenerateRequest();

        mockMvc.perform(post("/api/v1/cv/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.targetJobTitle", notNullValue()))
                .andExpect(jsonPath("$.errors.rawProfileText", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/cv/{id} - Récupération d'un CV existant")
    void testGetCvProfile() throws Exception {
        CvProfile profile = cvProfileRepository.save(CvProfile.builder()
                .sessionId("session-test-123")
                .targetCountry("FR")
                .content(new CvContent())
                .style(CvStyle.defaultStyle())
                .build());

        mockMvc.perform(get("/api/v1/cv/" + profile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(profile.getId().toString())));
    }

    @Test
    @DisplayName("PUT /api/v1/cv/{id}/style - Modification du style graphique")
    void testUpdateStyleEndpoint() throws Exception {
        CvProfile profile = cvProfileRepository.save(CvProfile.builder()
                .sessionId("session-style")
                .targetCountry("FR")
                .content(new CvContent())
                .style(CvStyle.defaultStyle())
                .build());

        CvStyleUpdateRequest styleUpdate = CvStyleUpdateRequest.builder()
                .primaryColor("#0f766e")
                .fontFamily("Times New Roman")
                .density(CvStyle.Density.COMPACT)
                .build();

        mockMvc.perform(put("/api/v1/cv/" + profile.getId() + "/style")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(styleUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.style.primaryColor", is("#0f766e")))
                .andExpect(jsonPath("$.style.fontFamily", is("Times New Roman")))
                .andExpect(jsonPath("$.style.density", is("COMPACT")));
    }

    @Test
    @DisplayName("POST /api/v1/cv/{id}/match - Évaluation de matching avec une offre")
    void testMatchEndpoint() throws Exception {
        CvProfile profile = cvProfileRepository.save(CvProfile.builder()
                .sessionId("session-match")
                .targetCountry("FR")
                .content(new CvContent())
                .style(CvStyle.defaultStyle())
                .build());

        AiMatchRequest matchRequest = new AiMatchRequest("Recherche ingénieur Java");

        mockMvc.perform(post("/api/v1/cv/" + profile.getId() + "/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchResult.matchScore", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/cv/{id}/photo - Upload de photo de profil")
    void testUploadPhotoEndpoint() throws Exception {
        CvProfile profile = cvProfileRepository.save(CvProfile.builder()
                .sessionId("session-photo")
                .targetCountry("FR")
                .content(new CvContent())
                .style(CvStyle.defaultStyle())
                .build());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/cv/" + profile.getId() + "/photo")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl", containsString("/api/v1/cv/photos/")));
    }

    @Test
    @DisplayName("GET /api/v1/cv/{id}/pdf - Téléchargement du PDF ATS")
    void testDownloadPdfEndpoint() throws Exception {
        CvProfile profile = cvProfileRepository.save(CvProfile.builder()
                .sessionId("session-pdf")
                .targetCountry("FR")
                .content(new CvContent())
                .style(CvStyle.defaultStyle())
                .build());

        mockMvc.perform(get("/api/v1/cv/" + profile.getId() + "/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("inline; filename=")));
    }
}
