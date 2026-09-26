package com.cvforge.service;

import com.cvforge.domain.CvContent;
import com.cvforge.domain.MatchResult;
import com.cvforge.dto.request.AiCvGenerateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AiCvServiceTest {

    @Autowired
    private AiCvService aiCvService;

    @Test
    @DisplayName("Génération structurée d'un contenu de CV complet avec bullets et compétences")
    void testGenerateCvContent() {
        AiCvGenerateRequest request = AiCvGenerateRequest.builder()
                .fullName("Florian Pépin")
                .email("florian@example.com")
                .phone("+33 6 11 22 33 44")
                .location("Paris")
                .targetJobTitle("Tech Lead Backend Java")
                .rawProfileText("7 ans d'expérience Java, Spring Boot, microservices, cloud, management d'équipe.")
                .rawJobOfferText("Recherche Tech Lead Java expérimenté pour concevoir architecture résiliente.")
                .targetCountry("FR")
                .build();

        CvContent content = aiCvService.generateCvContent(request);

        assertNotNull(content);
        assertNotNull(content.getContactInfo());
        assertNotNull(content.getProfessionalSummary());
        assertFalse(content.getExperiences().isEmpty(), "Le CV doit contenir au moins une expérience");
        assertFalse(content.getSkills().isEmpty(), "Le CV doit contenir des catégories de compétences");

        // Vérification de la présence de bullets d'action
        assertTrue(content.getExperiences().getFirst().getBullets().size() >= 2);
    }

    @Test
    @DisplayName("Analyse de matching avec calcul du score et extraction des mots-clés")
    void testAnalyzeJobMatch() {
        AiCvGenerateRequest req = AiCvGenerateRequest.builder()
                .targetJobTitle("Lead Developer")
                .rawProfileText("Java, Spring, Docker")
                .build();
        CvContent cvContent = aiCvService.generateCvContent(req);

        MatchResult match = aiCvService.analyzeJobMatch(cvContent, "Recherche Lead Developer Java Spring Boot Docker Kubernetes");

        assertNotNull(match);
        assertTrue(match.getMatchScore() > 0 && match.getMatchScore() <= 100);
        assertFalse(match.getMatchedKeywords().isEmpty());
    }
}
