package com.scholarship.finder.dto;

import com.scholarship.finder.entity.Document;

public class DocumentChecklistDto {
    private Long id;
    private Long userId;
    private Long schemeId;
    private String schemeNameEn;
    private String schemeNameTa;
    private Document document;
    private Boolean isReady;

    public DocumentChecklistDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public String getSchemeNameEn() { return schemeNameEn; }
    public void setSchemeNameEn(String schemeNameEn) { this.schemeNameEn = schemeNameEn; }

    public String getSchemeNameTa() { return schemeNameTa; }
    public void setSchemeNameTa(String schemeNameTa) { this.schemeNameTa = schemeNameTa; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public Boolean getIsReady() { return isReady; }
    public void setIsReady(Boolean isReady) { this.isReady = isReady; }
}

