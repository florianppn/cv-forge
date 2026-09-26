package com.cvforge.service;

import com.cvforge.domain.ContactInfo;
import com.cvforge.domain.CvContent;
import com.cvforge.domain.CvProfile;
import com.cvforge.domain.CvStyle;
import com.cvforge.domain.Education;
import com.cvforge.domain.Experience;
import com.cvforge.domain.MatchResult;
import com.cvforge.domain.SkillCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PdfExportServiceTest {

    @Autowired
    private PdfExportService pdfExportService;

    private CvProfile sampleProfile;

    @BeforeEach
    void setUp() {
        ContactInfo contact = ContactInfo.builder()
                .fullName("Florian Pépin")
                .email("florian@example.com")
                .phone("+33 6 12 34 56 78")
                .location("Paris, France")
                .linkedinUrl("https://linkedin.com/in/florian-pepin")
                .build();

        Experience exp1 = Experience.builder()
                .title("Architecte Logiciel")
                .company("Cloud Systems")
                .startDate("2021")
                .endDate("2024")
                .currentJob(true)
                .location("Paris")
                .bullets(Arrays.asList(
                        "Conception et déploiement de 15 microservices haute disponibilité.",
                        "Optimisation des temps de réponse de 40% sur les endpoints critiques."
                ))
                .build();

        Education edu1 = Education.builder()
                .degree("Diplôme d'Ingénieur")
                .institution("INSA")
                .location("Lyon")
                .graduationDate("2018")
                .details("Informatique et Télécommunications")
                .build();

        SkillCategory cat1 = SkillCategory.builder()
                .categoryName("Langages")
                .skills(Arrays.asList("Java 21", "TypeScript", "Python"))
                .build();

        CvContent content = CvContent.builder()
                .contactInfo(contact)
                .professionalSummary("Expert en ingénierie logicielle et conception d'APIs résilientes.")
                .experiences(Arrays.asList(exp1))
                .educations(Arrays.asList(edu1))
                .skills(Arrays.asList(cat1))
                .matchResult(MatchResult.builder().targetJobTitle("Architecte Java").matchScore(92).build())
                .build();

        sampleProfile = CvProfile.builder()
                .id(UUID.randomUUID())
                .sessionId(UUID.randomUUID().toString())
                .targetCountry("FR")
                .content(content)
                .style(CvStyle.defaultStyle())
                .build();
    }

    @Test
    @DisplayName("Génération réussie d'un document PDF ATS valide et sélectionnable")
    void testRenderCvPdf_Success() {
        byte[] pdfBytes = pdfExportService.renderCvPdf(sampleProfile);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000, "Le PDF généré doit contenir plus de 1000 octets");

        // Vérification de la signature magique du format PDF (%PDF-)
        String header = new String(pdfBytes, 0, 5, StandardCharsets.US_ASCII);
        assertTrue(header.startsWith("%PDF-"), "Le document doit être un PDF valide");
    }

    @Test
    @DisplayName("Génération PDF avec densités et polices de style personnalisées")
    void testRenderCvPdf_WithCustomStyles() {
        sampleProfile.getStyle().setDensity(CvStyle.Density.COMPACT);
        sampleProfile.getStyle().setFontFamily("Times New Roman");
        sampleProfile.getStyle().setBulletStyle(CvStyle.BulletStyle.SQUARE);
        sampleProfile.getStyle().setPrimaryColor("#0f172a");

        byte[] pdfBytes = pdfExportService.renderCvPdf(sampleProfile);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000);
    }

    @Test
    @DisplayName("Rejet si le profil ou son contenu est nul")
    void testRenderCvPdf_NullSafety() {
        assertThrows(IllegalArgumentException.class, () -> pdfExportService.renderCvPdf(null));

        CvProfile emptyProfile = new CvProfile();
        assertThrows(IllegalArgumentException.class, () -> pdfExportService.renderCvPdf(emptyProfile));
    }
}
