package com.scholarship.finder.dto;

import com.scholarship.finder.entity.Document;
import com.scholarship.finder.entity.SchemeEligibilityRule;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class SchemeDto {
    private Long id;
    private String nameEn;
    private String nameTa;
    private String descriptionEn;
    private String descriptionTa;
    private String benefits;
    private Double benefitAmount;
    private String benefitType;
    private String department;
    private String level;
    private String state;
    private String applyLink;
    private LocalDate openDate;
    private LocalDate lastDate;
    private String status;
    private Boolean renewalRequired;
    private List<String> educationLevels;
    private SchemeEligibilityRule eligibilityRule;
    private Set<Document> documents;

    public SchemeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameTa() { return nameTa; }
    public void setNameTa(String nameTa) { this.nameTa = nameTa; }

    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }

    public String getDescriptionTa() { return descriptionTa; }
    public void setDescriptionTa(String descriptionTa) { this.descriptionTa = descriptionTa; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public Double getBenefitAmount() { return benefitAmount; }
    public void setBenefitAmount(Double benefitAmount) { this.benefitAmount = benefitAmount; }

    public String getBenefitType() { return benefitType; }
    public void setBenefitType(String benefitType) { this.benefitType = benefitType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }

    public LocalDate getOpenDate() { return openDate; }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }

    public LocalDate getLastDate() { return lastDate; }
    public void setLastDate(LocalDate lastDate) { this.lastDate = lastDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getRenewalRequired() { return renewalRequired; }
    public void setRenewalRequired(Boolean renewalRequired) { this.renewalRequired = renewalRequired; }

    public List<String> getEducationLevels() { return educationLevels; }
    public void setEducationLevels(List<String> educationLevels) { this.educationLevels = educationLevels; }

    public SchemeEligibilityRule getEligibilityRule() { return eligibilityRule; }
    public void setEligibilityRule(SchemeEligibilityRule eligibilityRule) { this.eligibilityRule = eligibilityRule; }

    public Set<Document> getDocuments() { return documents; }
    public void setDocuments(Set<Document> documents) { this.documents = documents; }
}

