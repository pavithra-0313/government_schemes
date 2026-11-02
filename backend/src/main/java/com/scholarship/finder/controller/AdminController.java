package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.SchemeDto;
import com.scholarship.finder.entity.Document;
import com.scholarship.finder.repository.DocumentRepository;
import com.scholarship.finder.scheduler.DeadlineReminderScheduler;
import com.scholarship.finder.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Privileged CRUD endpoints for managing schemes, rules, documents, and reminders")
public class AdminController {

    private final SchemeService schemeService;
    private final DocumentRepository documentRepository;
    private final DeadlineReminderScheduler reminderScheduler;

    public AdminController(SchemeService schemeService,
                           DocumentRepository documentRepository,
                           DeadlineReminderScheduler reminderScheduler) {
        this.schemeService = schemeService;
        this.documentRepository = documentRepository;
        this.reminderScheduler = reminderScheduler;
    }

    @PostMapping("/schemes")
    @Operation(summary = "Create a new scheme with rules and education levels")
    public ResponseEntity<ApiResponse<SchemeDto>> createScheme(@Valid @RequestBody SchemeDto dto) {
        SchemeDto created = schemeService.createScheme(dto);
        return ResponseEntity.ok(ApiResponse.ok("Scheme created successfully", created));
    }

    @PutMapping("/schemes/{id}")
    @Operation(summary = "Update an existing scheme")
    public ResponseEntity<ApiResponse<SchemeDto>> updateScheme(@PathVariable Long id, @Valid @RequestBody SchemeDto dto) {
        SchemeDto updated = schemeService.updateScheme(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Scheme updated successfully", updated));
    }

    @DeleteMapping("/schemes/{id}")
    @Operation(summary = "Delete a scheme")
    public ResponseEntity<ApiResponse<Void>> deleteScheme(@PathVariable Long id) {
        schemeService.deleteScheme(id);
        return ResponseEntity.ok(ApiResponse.ok("Scheme deleted successfully", null));
    }

    @GetMapping("/documents")
    @Operation(summary = "List all documents")
    public ResponseEntity<ApiResponse<List<Document>>> getAllDocuments() {
        return ResponseEntity.ok(ApiResponse.ok(documentRepository.findAll()));
    }

    @PostMapping("/documents")
    @Operation(summary = "Create a document definition")
    public ResponseEntity<ApiResponse<Document>> createDocument(@RequestBody Document doc) {
        Document saved = documentRepository.save(doc);
        return ResponseEntity.ok(ApiResponse.ok("Document created successfully", saved));
    }

    @DeleteMapping("/documents/{id}")
    @Operation(summary = "Delete a document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        documentRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Document deleted successfully", null));
    }

    @PostMapping("/reminders/trigger")
    @Operation(summary = "Manually trigger the 7-day closing and renewal reminder email job")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> triggerReminders() {
        int closingSent = reminderScheduler.process7DayClosingReminders();
        int renewalsSent = reminderScheduler.processRenewalReminders();
        return ResponseEntity.ok(ApiResponse.ok("Reminder job executed",
                Map.of("closingRemindersDispatched", closingSent, "renewalRemindersDispatched", renewalsSent)));
    }
}

