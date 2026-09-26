package com.cvforge.service;

import com.cvforge.domain.ContactInfo;
import com.cvforge.domain.CvContent;
import com.cvforge.domain.Education;
import com.cvforge.domain.Experience;
import com.cvforge.domain.MatchResult;
import com.cvforge.domain.SkillCategory;
import com.cvforge.dto.request.AiCvGenerateRequest;
import com.cvforge.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AiCvService {

    private static final Logger log = LoggerFactory.getLogger(AiCvService.class);

    private final ChatClient chatClient;
    private final String apiKey;

    public AiCvService(ChatClient.Builder chatClientBuilder,
                       @Value("${spring.ai.openai.api-key:demo-key}") String apiKey) {
        this.chatClient = chatClientBuilder.build();
        this.apiKey = apiKey;
    }

    private static final String CV_GENERATION_SYSTEM_PROMPT = """
            Tu es un expert mondial en recrutement technique et optimisation de CV pour les Applicant Tracking Systems (ATS).
            Ta mission est de transformer la description brute du parcours de l'utilisateur en un CV professionnel hautement qualifié, structuré et optimisé pour le poste visé.

            Consignes strictes d'optimisation ATS :
            1. Rédige un résumé professionnel percutant de 2 à 3 phrases orienté impact et proposition de valeur.
            2. Pour chaque expérience professionnelle :
               - Formule 3 à 5 bullets d'accomplissement commençant obligatoirement par un verbe d'action dynamique (ex: 'Architecturé', 'Optimisé', 'Déployé', 'Accéléré', 'Réduit').
               - Quantifie au maximum les résultats (pourcentages de gains, volumes de données, métriques de performance, taille d'équipe).
               - Intègre naturellement les mots-clés techniques clés du poste ciblé.
            3. Regroupe les compétences par catégories claires (ex: 'Langages & Frameworks', 'Cloud & Architecture', 'Outils & DevOps').
            4. Si une offre d'emploi est fournie, maximise la concordance de vocabulaire et remplis le bloc matchResult avec le score et les mots-clés.
            """;

    private static final String MATCHING_SYSTEM_PROMPT = """
            Tu es un système ATS d'évaluation et de matching de candidatures.
            Analyse le CV fourni en regard de l'offre d'emploi transmise.
            Évalue précisément l'adéquation :
            1. Calcule un matchScore objectif entre 0 et 100.
            2. Identifie les matchedKeywords (mots-clés requis présents dans le CV).
            3. Identifie les missingKeywords (mots-clés importants de l'offre absents du CV).
            4. Fournis 2 à 3 points forts (strengths) et 2 à 3 axes d'amélioration concrets (suggestions) pour passer les filtres ATS.
            """;

    public CvContent generateCvContent(AiCvGenerateRequest request) {
        if (isDemoOrOffline()) {
            log.info("Mode démo ou hors-ligne détecté : génération du contenu structuré mocké.");
            return generateMockCvContent(request);
        }

        try {
            String userPrompt = buildCvPrompt(request);

            CvContent generated = chatClient.prompt()
                    .system(CV_GENERATION_SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .entity(CvContent.class);

            if (generated == null) {
                throw new AiServiceException("Le modèle IA a retourné une réponse vide.");
            }

            if (generated.getContactInfo() == null) {
                generated.setContactInfo(new ContactInfo());
            }
            enrichContactInfo(generated.getContactInfo(), request);

            return generated;
        } catch (Exception e) {
            log.warn("Erreur lors de l'appel au modèle Spring AI OpenAI ({}). Bascule sur le générateur de secours.", e.getMessage());
            return generateMockCvContent(request);
        }
    }

    public MatchResult analyzeJobMatch(CvContent cvContent, String jobOfferText) {
        if (isDemoOrOffline()) {
            return generateMockMatchResult(cvContent, jobOfferText);
        }

        try {
            String userPrompt = String.format("""
                    CV ACTUEL :
                    Titre professionnel : %s
                    Résumé : %s
                    Expériences : %s
                    Compétences : %s

                    OFFRE D'EMPLOI CIBLÉE :
                    %s
                    """,
                    cvContent.getContactInfo() != null ? cvContent.getContactInfo().getFullName() : "",
                    cvContent.getProfessionalSummary(),
                    cvContent.getExperiences(),
                    cvContent.getSkills(),
                    jobOfferText
            );

            MatchResult result = chatClient.prompt()
                    .system(MATCHING_SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .entity(MatchResult.class);

            if (result == null) {
                throw new AiServiceException("Le modèle IA a retourné une analyse de matching vide.");
            }

            return result;
        } catch (Exception e) {
            log.warn("Erreur lors de l'analyse IA de matching ({}). Bascule sur analyse de repli.", e.getMessage());
            return generateMockMatchResult(cvContent, jobOfferText);
        }
    }

    private boolean isDemoOrOffline() {
        return apiKey == null || apiKey.isBlank() || apiKey.equals("demo-key") || apiKey.equals("test-key") || apiKey.startsWith("sk-proj-votre");
    }

    private String buildCvPrompt(AiCvGenerateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Nom du candidat : ").append(request.getFullName() != null ? request.getFullName() : "Non précisé").append("\n");
        sb.append("Poste visé : ").append(request.getTargetJobTitle()).append("\n");
        sb.append("Pays ciblé : ").append(request.getTargetCountry()).append("\n");
        sb.append("Description du parcours / données brutes :\n").append(request.getRawProfileText()).append("\n");

        if (request.getRawJobOfferText() != null && !request.getRawJobOfferText().isBlank()) {
            sb.append("\nOffre d'emploi ciblée pour optimisation ATS :\n").append(request.getRawJobOfferText()).append("\n");
        }
        return sb.toString();
    }

    private void enrichContactInfo(ContactInfo contact, AiCvGenerateRequest request) {
        if (request.getFullName() != null && !request.getFullName().isBlank()) contact.setFullName(request.getFullName());
        if (request.getEmail() != null && !request.getEmail().isBlank()) contact.setEmail(request.getEmail());
        if (request.getPhone() != null && !request.getPhone().isBlank()) contact.setPhone(request.getPhone());
        if (request.getLocation() != null && !request.getLocation().isBlank()) contact.setLocation(request.getLocation());
        if (request.getLinkedinUrl() != null && !request.getLinkedinUrl().isBlank()) contact.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getPortfolioUrl() != null && !request.getPortfolioUrl().isBlank()) contact.setPortfolioUrl(request.getPortfolioUrl());
    }

    private CvContent generateMockCvContent(AiCvGenerateRequest request) {
        ContactInfo contact = ContactInfo.builder()
                .fullName(request.getFullName() != null ? request.getFullName() : "Florian Développeur")
                .email(request.getEmail() != null ? request.getEmail() : "florian.pepin@example.com")
                .phone(request.getPhone() != null ? request.getPhone() : "+33 6 12 34 56 78")
                .location(request.getLocation() != null ? request.getLocation() : "Paris, France")
                .linkedinUrl(request.getLinkedinUrl() != null ? request.getLinkedinUrl() : "https://linkedin.com/in/florian-pepin")
                .portfolioUrl(request.getPortfolioUrl() != null ? request.getPortfolioUrl() : "https://github.com/florian")
                .build();

        String summary = String.format(
                "Ingénieur logiciel senior spécialisé en architectures distribuées et backend robuste. " +
                "Expert dans la conception d'APIs scalables et de systèmes haute disponibilité pour le poste de %s. " +
                "Reconnu pour optimiser les performances des applications critiques et piloter des équipes agiles vers l'excellence technique.",
                request.getTargetJobTitle()
        );

        List<Experience> experiences = new ArrayList<>();
        experiences.add(Experience.builder()
                .title(request.getTargetJobTitle())
                .company("Tech Innovations SAS")
                .location("Paris, France")
                .startDate("Janv. 2022")
                .endDate("Présent")
                .currentJob(true)
                .bullets(Arrays.asList(
                        "Architecturé et déployé une plateforme de microservices traitant plus de 5 millions de requêtes quotidiennes avec un temps de réponse p99 < 120ms.",
                        "Optimisé les pipelines CI/CD sous Docker et Kubernetes, réduisant le délai moyen de mise en production de 45%.",
                        "Piloté une équipe de 6 développeurs dans l'adoption du Domain-Driven Design (DDD) et de la couverture de tests à 85%."
                ))
                .build());

        experiences.add(Experience.builder()
                .title("Développeur Backend Java")
                .company("Digital Cloud Solutions")
                .location("Lyon, France")
                .startDate("Sept. 2019")
                .endDate("Déc. 2021")
                .currentJob(false)
                .bullets(Arrays.asList(
                        "Conçu et maintenu 12 APIs RESTful critiques sous Spring Boot assurant un taux de disponibilité mesuré à 99,98%.",
                        "Restructuré les requêtes SQL et index PostgreSQL, accélérant le traitement des exports de données complexes de 60%."
                ))
                .build());

        List<Education> educations = new ArrayList<>();
        educations.add(Education.builder()
                .degree("Diplôme d'Ingénieur en Informatique")
                .institution("INSA / École d'Ingénieurs")
                .location("France")
                .startDate("2016")
                .graduationDate("2019")
                .details("Spécialisation Systèmes d'Information et Architectures Logicielles")
                .build());

        List<SkillCategory> skills = new ArrayList<>();
        skills.add(SkillCategory.builder()
                .categoryName("Langages & Frameworks")
                .skills(Arrays.asList("Java 21", "Spring Boot 3", "Spring AI", "TypeScript", "Angular", "Python"))
                .build());
        skills.add(SkillCategory.builder()
                .categoryName("Bases de données & Cloud")
                .skills(Arrays.asList("PostgreSQL (JSONB)", "Redis", "Docker", "Kubernetes", "AWS"))
                .build());
        skills.add(SkillCategory.builder()
                .categoryName("Méthodologies & Outils")
                .skills(Arrays.asList("CI/CD (GitLab, GitHub Actions)", "Clean Architecture", "TDD", "Git", "Maven"))
                .build());

        MatchResult matchResult = null;
        if (request.getRawJobOfferText() != null && !request.getRawJobOfferText().isBlank()) {
            matchResult = MatchResult.builder()
                    .targetJobTitle(request.getTargetJobTitle())
                    .matchScore(88)
                    .matchedKeywords(Arrays.asList("Java", "Spring Boot", "API REST", "PostgreSQL", "Docker", "Architecture"))
                    .missingKeywords(Arrays.asList("Kafka", "GraphQL"))
                    .strengths(Arrays.asList(
                            "Forte quantification des réalisations chiffrées sur les expériences clés.",
                            "Parfaite adéquation de la stack technique principale (Spring Boot, Cloud, Microservices)."
                    ))
                    .suggestions(Arrays.asList(
                            "Intégrer une mention d'outils de streaming d'événements (Kafka/RabbitMQ) si pratiqués.",
                            "Mettre en exergue les pratiques d'observabilité (OpenTelemetry, Prometheus)."
                    ))
                    .build();
        }

        return CvContent.builder()
                .contactInfo(contact)
                .professionalSummary(summary)
                .experiences(experiences)
                .educations(educations)
                .skills(skills)
                .matchResult(matchResult)
                .build();
    }

    private MatchResult generateMockMatchResult(CvContent cvContent, String jobOfferText) {
        return MatchResult.builder()
                .targetJobTitle("Poste ciblé")
                .matchScore(85)
                .matchedKeywords(Arrays.asList("Java", "Spring Boot", "PostgreSQL", "API REST", "Docker"))
                .missingKeywords(Arrays.asList("Event-driven Architecture", "Monitoring"))
                .strengths(Arrays.asList(
                        "Profil technique très aligné avec les exigences fondamentales de l'offre.",
                        "Structure ATS claire avec verbes d'action et métriques quantifiées."
                ))
                .suggestions(Arrays.asList(
                        "Ajouter les technologies secondaires mentionnées dans l'annonce dans la section Compétences."
                ))
                .build();
    }
}
