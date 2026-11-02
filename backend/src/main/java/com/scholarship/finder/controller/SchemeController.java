package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.SchemeDto;
import com.scholarship.finder.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
@Tag(name = "Schemes", description = "Public catalog, search, and detailed information about scholarships and welfare schemes")
public class SchemeController {

    private final SchemeService schemeService;

    public SchemeController(SchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @GetMapping
    @Operation(summary = "List all schemes or filter by multiple criteria")
    public ResponseEntity<ApiResponse<List<SchemeDto>>> getSchemes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String schemeLevel,
            @RequestParam(required = false) String benefitType,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String department) {

        List<SchemeDto> schemes;
        if (keyword != null || educationLevel != null || schemeLevel != null || benefitType != null || state != null || department != null) {
            schemes = schemeService.searchSchemes(keyword, educationLevel, schemeLevel, benefitType, state, department);
        } else {
            schemes = schemeService.getAllSchemes();
        }
        return ResponseEntity.ok(ApiResponse.ok(schemes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get scheme details by ID including required documents and rules")
    public ResponseEntity<ApiResponse<SchemeDto>> getSchemeById(@PathVariable Long id) {
        SchemeDto scheme = schemeService.getSchemeById(id);
        return ResponseEntity.ok(ApiResponse.ok(scheme));
    }
}

