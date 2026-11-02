package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.CalendarEventDto;
import com.scholarship.finder.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Scholarship Calendar", description = "Timeline and upcoming deadlines for matched and saved schemes")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    @Operation(summary = "Get calendar events (openings, closings, renewals) for matched/saved schemes")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> getCalendar(Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getCredentials() instanceof Long) {
            userId = (Long) authentication.getCredentials();
        }
        List<CalendarEventDto> events = calendarService.getCalendarEventsForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(events));
    }
}

