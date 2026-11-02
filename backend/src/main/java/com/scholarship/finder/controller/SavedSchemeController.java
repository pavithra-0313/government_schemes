package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.SavedSchemeDto;
import com.scholarship.finder.service.SavedSchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-schemes")
@Tag(name = "Saved Schemes & Application Tracker", description = "Endpoints for bookmarking schemes and tracking application statuses")
public class SavedSchemeController {

    private final SavedSchemeService savedSchemeService;

    public SavedSchemeController(SavedSchemeService savedSchemeService) {
        this.savedSchemeService = savedSchemeService;
    }

    @GetMapping
    @Operation(summary = "Get all saved schemes for the authenticated student")
    public ResponseEntity<ApiResponse<List<SavedSchemeDto>>> getSavedSchemes(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        List<SavedSchemeDto> list = savedSchemeService.getSavedSchemesForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/{schemeId}")
    @Operation(summary = "Save / Bookmark a scheme and initialize document checklist")
    public ResponseEntity<ApiResponse<SavedSchemeDto>> saveScheme(@PathVariable Long schemeId, Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        SavedSchemeDto saved = savedSchemeService.saveScheme(userId, schemeId);
        return ResponseEntity.ok(ApiResponse.ok("Scheme saved to your tracker", saved));
    }

    @PatchMapping("/{schemeId}/status")
    @Operation(summary = "Update application status (Saved, In Progress, Applied, Approved, Rejected)")
    public ResponseEntity<ApiResponse<SavedSchemeDto>> updateStatus(
            @PathVariable Long schemeId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        String status = body.get("status");
        SavedSchemeDto updated = savedSchemeService.updateStatus(userId, schemeId, status);
        return ResponseEntity.ok(ApiResponse.ok("Application status updated", updated));
    }

    @DeleteMapping("/{schemeId}")
    @Operation(summary = "Remove scheme from saved tracker")
    public ResponseEntity<ApiResponse<Void>> removeScheme(@PathVariable Long schemeId, Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        savedSchemeService.removeSavedScheme(userId, schemeId);
        return ResponseEntity.ok(ApiResponse.ok("Scheme removed from tracker", null));
    }
}

