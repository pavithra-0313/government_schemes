package com.scholarship.finder.dto;

import java.time.LocalDate;

public class CalendarEventDto {
    private Long schemeId;
    private String schemeNameEn;
    private String schemeNameTa;
    private String eventType; // OPENING, CLOSING, RENEWAL
    private LocalDate date;
    private long daysRemaining;
    private String applyLink;
    private Double benefitAmount;
    private String benefitType;
    private boolean isSaved;

    public CalendarEventDto() {}

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public String getSchemeNameEn() { return schemeNameEn; }
    public void setSchemeNameEn(String schemeNameEn) { this.schemeNameEn = schemeNameEn; }

    public String getSchemeNameTa() { return schemeNameTa; }
    public void setSchemeNameTa(String schemeNameTa) { this.schemeNameTa = schemeNameTa; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }

    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }

    public Double getBenefitAmount() { return benefitAmount; }
    public void setBenefitAmount(Double benefitAmount) { this.benefitAmount = benefitAmount; }

    public String getBenefitType() { return benefitType; }
    public void setBenefitType(String benefitType) { this.benefitType = benefitType; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
}

