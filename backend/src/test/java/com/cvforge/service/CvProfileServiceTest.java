package com.cvforge.service;

import com.cvforge.domain.CvContent;
import com.cvforge.domain.CvProfile;
import com.cvforge.domain.CvStyle;
import com.cvforge.domain.MatchResult;
import com.cvforge.dto.request.AiCvGenerateRequest;
import com.cvforge.dto.request.CvStyleUpdateRequest;
import com.cvforge.dto.response.CvProfileResponse;
import com.cvforge.repository.CvProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CvProfileServiceTest {

    @Autowired
    private CvProfileService cvProfileService;

    @Autowired
    private CvProfileRepository cvProfileRepository;

    @Test
    @DisplayName("Création, persistance et cycle de vie d'un CV")
    void testCreateAndLifecycle() {
        String sessionId = UUID.randomUUID().toString();
        AiCvGenerateRequest request = AiCvGenerateRequest.builder()
                .fullName("Florian Pépin")
                .email("florian@example.com")
                .targetJobTitle("Architecte Logiciel")
                .rawProfileText("Expertise Java, architectures cloud, API REST")
                .targetCountry("FR")
                .sessionId(sessionId)
                .build();

        CvProfileResponse response = cvProfileService.createAndGenerateCv(request, sessionId);

        assertNotNull(response.getId());
        assertEquals(sessionId, response.getSessionId());
        assertNotNull(response.getContent());
        assertNotNull(response.getStyle());

        // Récupération par session
        List<CvProfileResponse> list = cvProfileService.listProfilesBySession(sessionId);
        assertFalse(list.isEmpty());
        assertEquals(response.getId(), list.getFirst().getId());

        // Mise à jour isolée du style (sans appel IA)
        CvStyleUpdateRequest styleUpdate = CvStyleUpdateRequest.builder()
                .primaryColor("#2b6cb0")
                .density(CvStyle.Density.SPACIOUS)
                .fontFamily("Arial")
                .build();

        CvProfileResponse updated = cvProfileService.updateStyle(response.getId(), styleUpdate);
        assertEquals("#2b6cb0", updated.getStyle().getPrimaryColor());
        assertEquals(CvStyle.Density.SPACIOUS, updated.getStyle().getDensity());
        assertEquals("Arial", updated.getStyle().getFontFamily());

        // Mise à jour manuelle du contenu
        CvContent manualContent = updated.getContent();
        manualContent.setProfessionalSummary("Nouveau résumé professionnel édité à la main.");
        CvProfileResponse contentUpdated = cvProfileService.updateContent(response.getId(), manualContent);
        assertEquals("Nouveau résumé professionnel édité à la main.", contentUpdated.getContent().getProfessionalSummary());

        // Matching offre d'emploi
        MatchResult match = cvProfileService.analyzeJobMatch(response.getId(), "Offre Tech Lead");
        assertNotNull(match);
        assertTrue(match.getMatchScore() > 0);
    }

    @Test
    @DisplayName("Application de la règle ATS par pays : masquage de la photo pour US/UK")
    void testAtsCountryRule_PhotoSuppressedForUS() {
        String sessionId = UUID.randomUUID().toString();
        AiCvGenerateRequest request = AiCvGenerateRequest.builder()
                .fullName("John Doe")
                .targetJobTitle("Senior Software Engineer")
                .rawProfileText("Fullstack engineer with 10 years experience")
                .targetCountry("US")
                .sessionId(sessionId)
                .build();

        CvProfileResponse response = cvProfileService.createAndGenerateCv(request, sessionId);
        assertFalse(response.getStyle().getShowPhoto(), "Pour un CV ciblé USA, la photo doit être désactivée par défaut pour conformité ATS");
    }
}
