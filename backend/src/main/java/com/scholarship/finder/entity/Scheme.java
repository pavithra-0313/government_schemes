package com.scholarship.finder.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "schemes")
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false, length = 500)
    private String nameEn;

    @Column(name = "name_ta", nullable = false, length = 500)
    private String nameTa;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_ta", columnDefinition = "TEXT")
    private String descriptionTa;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "benefit_amount")
    private Double benefitAmount;

    @Column(name = "benefit_type", nullable = false)
    private String benefitType; // Scholarship, Fee Waiver, Loan, Skill Training, Free Items

    private String department;

    @Column(nullable = false)
    private String level; // Central, State

    private String state;

    @Column(name = "apply_link")
    private String applyLink;

    @Column(name = "open_date")
    private LocalDate openDate;

    @Column(name = "last_date")
    private LocalDate lastDate;

    private String status = "ACTIVE";

    @Column(name = "renewal_required")
    private Boolean renewalRequired = false;

    @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SchemeEducationLevel> educationLevels = new ArrayList<>();

    @OneToOne(mappedBy = "scheme", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private SchemeEligibilityRule eligibilityRule;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "scheme_documents",
        joinColumns = @JoinColumn(name = "scheme_id"),
        inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private Set<Document> documents = new HashSet<>();

    public Scheme() {}

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

    public List<SchemeEducationLevel> getEducationLevels() { return educationLevels; }
    public void setEducationLevels(List<SchemeEducationLevel> educationLevels) { this.educationLevels = educationLevels; }

    public SchemeEligibilityRule getEligibilityRule() { return eligibilityRule; }
    public void setEligibilityRule(SchemeEligibilityRule eligibilityRule) { this.eligibilityRule = eligibilityRule; }

    public Set<Document> getDocuments() { return documents; }
    public void setDocuments(Set<Document> documents) { this.documents = documents; }
}

