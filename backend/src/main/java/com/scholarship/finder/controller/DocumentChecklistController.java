package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.DocumentChecklistDto;
import com.scholarship.finder.service.DocumentChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checklist")
@Tag(name = "Document Checklist", description = "Endpoints for ticking and unticking required documents per scheme")
public class DocumentChecklistController {

    private final DocumentChecklistService checklistService;

    public DocumentChecklistController(DocumentChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @GetMapping
    @Operation(summary = "Get all document checklist items for the student across saved schemes")
    public ResponseEntity<ApiResponse<List<DocumentChecklistDto>>> getAllChecklists(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        List<DocumentChecklistDto> list = checklistService.getAllChecklistsForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/scheme/{schemeId}")
    @Operation(summary = "Get checklist items for a specific saved scheme")
    public ResponseEntity<ApiResponse<List<DocumentChecklistDto>>> getSchemeChecklist(
            @PathVariable Long schemeId,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        List<DocumentChecklistDto> list = checklistService.getChecklistForScheme(userId, schemeId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PutMapping("/{schemeId}/{documentId}")
    @Operation(summary = "Toggle document readiness state (ready/pending)")
    public ResponseEntity<ApiResponse<DocumentChecklistDto>> updateDocumentStatus(
            @PathVariable Long schemeId,
            @PathVariable Long documentId,
            @RequestBody Map<String, Boolean> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        boolean isReady = body.getOrDefault("isReady", false);
        DocumentChecklistDto updated = checklistService.updateDocumentReady(userId, schemeId, documentId, isReady);
        return ResponseEntity.ok(ApiResponse.ok("Document readiness updated", updated));
    }
}

