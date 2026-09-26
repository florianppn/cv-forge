package com.cvforge.service;

import com.cvforge.domain.CvProfile;
import com.cvforge.exception.AiServiceException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class PdfExportService {

    private static final Logger log = LoggerFactory.getLogger(PdfExportService.class);

    private final SpringTemplateEngine templateEngine;
    private final FileStorageService fileStorageService;

    public PdfExportService(SpringTemplateEngine templateEngine, FileStorageService fileStorageService) {
        this.templateEngine = templateEngine;
        this.fileStorageService = fileStorageService;
    }

    public byte[] renderCvPdf(CvProfile profile) {
        if (profile == null || profile.getContent() == null) {
            throw new IllegalArgumentException("Le profil et son contenu ne peuvent pas être nuls pour la génération PDF.");
        }

        try {
            Context context = new Context();
            context.setVariable("profile", profile);
            context.setVariable("content", profile.getContent());
            context.setVariable("style", profile.getStyle());

            String photoDataUri = null;
            if (Boolean.TRUE.equals(profile.getStyle().getShowPhoto()) && profile.getPhotoUrl() != null) {
                photoDataUri = extractPhotoDataUri(profile.getPhotoUrl());
            }
            context.setVariable("photoDataUri", photoDataUri);

            String processedHtml = templateEngine.process("pdf/cv-ats-template", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(processedHtml, "");
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour le profil {} : {}", profile.getId(), e.getMessage(), e);
            throw new AiServiceException("Échec de la génération du document PDF : " + e.getMessage(), e);
        }
    }

    private String extractPhotoDataUri(String photoUrl) {
        try {
            String filename = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
            Resource resource = fileStorageService.loadAsResource(filename);
            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                String mimeType = filename.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
                return "data:" + mimeType + ";base64," + base64;
            }
        } catch (IOException e) {
            log.warn("Impossible de charger la photo pour l'export PDF ({}). Le PDF sera généré sans photo.", e.getMessage());
            return null;
        }
    }
}
