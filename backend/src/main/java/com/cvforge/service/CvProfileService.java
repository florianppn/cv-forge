package com.cvforge.service;

import com.cvforge.domain.CvContent;
import com.cvforge.domain.CvProfile;
import com.cvforge.domain.CvStyle;
import com.cvforge.domain.MatchResult;
import com.cvforge.dto.request.AiCvGenerateRequest;
import com.cvforge.dto.request.CvStyleUpdateRequest;
import com.cvforge.dto.response.CvProfileResponse;
import com.cvforge.exception.ResourceNotFoundException;
import com.cvforge.repository.CvProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CvProfileService {

    private static final Logger log = LoggerFactory.getLogger(CvProfileService.class);

    private final CvProfileRepository cvProfileRepository;
    private final AiCvService aiCvService;
    private final PdfExportService pdfExportService;
    private final FileStorageService fileStorageService;

    public CvProfileService(CvProfileRepository cvProfileRepository,
                            AiCvService aiCvService,
                            PdfExportService pdfExportService,
                            FileStorageService fileStorageService) {
        this.cvProfileRepository = cvProfileRepository;
        this.aiCvService = aiCvService;
        this.pdfExportService = pdfExportService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public CvProfileResponse createAndGenerateCv(AiCvGenerateRequest request, String clientSessionId) {
        String effectiveSessionId = (request.getSessionId() != null && !request.getSessionId().isBlank())
                ? request.getSessionId()
                : (clientSessionId != null ? clientSessionId : UUID.randomUUID().toString());

        log.info("Génération d'un nouveau CV par l'IA pour la session {}", effectiveSessionId);

        CvContent generatedContent = aiCvService.generateCvContent(request);

        CvProfile profile = CvProfile.builder()
                .sessionId(effectiveSessionId)
                .userEmail(request.getEmail())
                .targetCountry(request.getTargetCountry())
                .content(generatedContent)
                .style(CvStyle.defaultStyle())
                .build();

        CvProfile saved = cvProfileRepository.save(profile);
        return CvProfileResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public CvProfileResponse getProfile(UUID id) {
        CvProfile profile = findEntityById(id);
        return CvProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public List<CvProfileResponse> listProfilesBySession(String sessionId) {
        return cvProfileRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .map(CvProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CvProfileResponse updateContent(UUID id, CvContent newContent) {
        CvProfile profile = findEntityById(id);
        profile.setContent(newContent);
        CvProfile updated = cvProfileRepository.save(profile);
        log.info("Contenu du profil {} mis à jour manuellement sans appel IA.", id);
        return CvProfileResponse.fromEntity(updated);
    }

    @Transactional
    public CvProfileResponse updateStyle(UUID id, CvStyleUpdateRequest request) {
        CvProfile profile = findEntityById(id);
        CvStyle style = profile.getStyle() != null ? profile.getStyle() : CvStyle.defaultStyle();

        if (request.getPrimaryColor() != null) style.setPrimaryColor(request.getPrimaryColor());
        if (request.getFontFamily() != null) style.setFontFamily(request.getFontFamily());
        if (request.getDensity() != null) style.setDensity(request.getDensity());
        if (request.getBulletStyle() != null) style.setBulletStyle(request.getBulletStyle());
        if (request.getSectionOrder() != null && !request.getSectionOrder().isEmpty()) {
            style.setSectionOrder(request.getSectionOrder());
        }
        if (request.getShowPhoto() != null) {
            style.setShowPhoto(request.getShowPhoto());
        }

        profile.setStyle(style);
        CvProfile updated = cvProfileRepository.save(profile);
        log.info("Style du profil {} mis à jour sans appel IA.", id);
        return CvProfileResponse.fromEntity(updated);
    }

    @Transactional
    public MatchResult analyzeJobMatch(UUID id, String jobOfferText) {
        CvProfile profile = findEntityById(id);
        log.info("Analyse de matching avec une offre d'emploi pour le profil {}", id);

        MatchResult matchResult = aiCvService.analyzeJobMatch(profile.getContent(), jobOfferText);

        if (profile.getContent() != null) {
            profile.getContent().setMatchResult(matchResult);
            cvProfileRepository.save(profile);
        }

        return matchResult;
    }

    @Transactional
    public CvProfileResponse uploadPhoto(UUID id, MultipartFile file) {
        CvProfile profile = findEntityById(id);
        String photoUrl = fileStorageService.storeAvatar(file);
        profile.setPhotoUrl(photoUrl);

        CvProfile saved = cvProfileRepository.save(profile);
        log.info("Photo uploadée avec succès pour le profil {} : {}", id, photoUrl);
        return CvProfileResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public byte[] exportPdf(UUID id) {
        CvProfile profile = findEntityById(id);
        return pdfExportService.renderCvPdf(profile);
    }

    private CvProfile findEntityById(UUID id) {
        return cvProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profil CV introuvable avec l'identifiant : " + id));
    }
}
