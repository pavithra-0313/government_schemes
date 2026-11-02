package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.SchemeMatchResultDto;
import com.scholarship.finder.dto.StudentProfileDto;
import com.scholarship.finder.service.EligibilityEngineService;
import com.scholarship.finder.service.StudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eligibility")
@Tag(name = "Eligibility Engine", description = "Rule-matching engine providing scores and match explanations")
public class EligibilityController {

    private final EligibilityEngineService eligibilityEngine;
    private final StudentProfileService profileService;

    public EligibilityController(EligibilityEngineService eligibilityEngine, StudentProfileService profileService) {
        this.eligibilityEngine = eligibilityEngine;
        this.profileService = profileService;
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate schemes for any student profile (authenticated or guest)")
    public ResponseEntity<ApiResponse<List<SchemeMatchResultDto>>> evaluateProfile(
            @RequestBody StudentProfileDto profileDto) {
        List<SchemeMatchResultDto> results = eligibilityEngine.evaluateEligibility(profileDto);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/my-matches")
    @Operation(summary = "Evaluate schemes for the logged-in student's existing saved profile")
    public ResponseEntity<ApiResponse<List<SchemeMatchResultDto>>> getMyMatches(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
        }
        Long userId = (Long) authentication.getCredentials();
        if (!profileService.hasProfile(userId)) {
            return ResponseEntity.ok(ApiResponse.ok("No profile completed yet", List.of()));
        }
        StudentProfileDto profile = profileService.getProfileByUserId(userId);
        List<SchemeMatchResultDto> results = eligibilityEngine.evaluateEligibility(profile);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }
}

