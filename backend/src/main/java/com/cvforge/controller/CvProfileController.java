package com.cvforge.controller;

import com.cvforge.domain.MatchResult;
import com.cvforge.dto.request.AiCvGenerateRequest;
import com.cvforge.dto.request.AiMatchRequest;
import com.cvforge.dto.request.CvContentUpdateRequest;
import com.cvforge.dto.request.CvStyleUpdateRequest;
import com.cvforge.dto.response.CvProfileResponse;
import com.cvforge.dto.response.MatchResponse;
import com.cvforge.service.CvProfileService;
import com.cvforge.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cv")
public class CvProfileController {

    private final CvProfileService cvProfileService;
    private final FileStorageService fileStorageService;

    public CvProfileController(CvProfileService cvProfileService, FileStorageService fileStorageService) {
        this.cvProfileService = cvProfileService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/generate")
    public ResponseEntity<CvProfileResponse> generateCv(
            @Valid @RequestBody AiCvGenerateRequest request,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionIdHeader) {

        CvProfileResponse response = cvProfileService.createAndGenerateCv(request, sessionIdHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CvProfileResponse> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(cvProfileService.getProfile(id));
    }

    @GetMapping
    public ResponseEntity<List<CvProfileResponse>> listProfilesBySession(
            @RequestParam("sessionId") String sessionId) {
        return ResponseEntity.ok(cvProfileService.listProfilesBySession(sessionId));
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<CvProfileResponse> updateContent(
            @PathVariable UUID id,
            @Valid @RequestBody CvContentUpdateRequest request) {
        return ResponseEntity.ok(cvProfileService.updateContent(id, request.getContent()));
    }

    @PutMapping("/{id}/style")
    public ResponseEntity<CvProfileResponse> updateStyle(
            @PathVariable UUID id,
            @Valid @RequestBody CvStyleUpdateRequest request) {
        return ResponseEntity.ok(cvProfileService.updateStyle(id, request));
    }

    @PostMapping("/{id}/match")
    public ResponseEntity<MatchResponse> matchWithJobOffer(
            @PathVariable UUID id,
            @Valid @RequestBody AiMatchRequest request) {
        MatchResult matchResult = cvProfileService.analyzeJobMatch(id, request.getJobOfferText());
        return ResponseEntity.ok(new MatchResponse(matchResult));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CvProfileResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(cvProfileService.uploadPhoto(id, file));
    }

    @GetMapping("/photos/{filename:.+}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String filename) {
        Resource file = fileStorageService.loadAsResource(filename);
        String contentType = filename.toLowerCase().endsWith(".png") ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(file);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        byte[] pdfBytes = cvProfileService.exportPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"cv-" + id + ".pdf\"")
                .body(pdfBytes);
    }
}
