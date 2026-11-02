package com.scholarship.finder.dto;

import java.time.LocalDateTime;

public class SavedSchemeDto {
    private Long id;
    private Long userId;
    private Long schemeId;
    private SchemeDto scheme;
    private String applicationStatus; // Saved, In Progress, Applied, Approved, Rejected
    private LocalDateTime savedAt;

    public SavedSchemeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public SchemeDto getScheme() { return scheme; }
    public void setScheme(SchemeDto scheme) { this.scheme = scheme; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}

