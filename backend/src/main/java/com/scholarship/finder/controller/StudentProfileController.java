package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.StudentProfileDto;
import com.scholarship.finder.service.StudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Student Profile", description = "Endpoints for managing demographic, academic, and socio-economic student profiles")
public class StudentProfileController {

    private final StudentProfileService profileService;

    public StudentProfileController(StudentProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the student profile for the authenticated user")
    public ResponseEntity<ApiResponse<StudentProfileDto>> getMyProfile(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        if (!profileService.hasProfile(userId)) {
            return ResponseEntity.ok(ApiResponse.ok("No profile found", null));
        }
        StudentProfileDto profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PostMapping
    @PutMapping
    @Operation(summary = "Create or update student profile")
    public ResponseEntity<ApiResponse<StudentProfileDto>> saveProfile(
            @Valid @RequestBody StudentProfileDto profileDto,
            Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        StudentProfileDto saved = profileService.createOrUpdateProfile(userId, profileDto);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", saved));
    }
}

