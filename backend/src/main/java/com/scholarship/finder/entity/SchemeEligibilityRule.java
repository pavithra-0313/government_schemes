package com.scholarship.finder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "scheme_eligibility_rules")
public class SchemeEligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    @JsonIgnore
    private Scheme scheme;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "max_income")
    private Double maxIncome;

    private String gender; // Male, Female, or null

    @Column(name = "community_category")
    private String communityCategory; // Comma separated, e.g. "SC,ST"

    @Column(name = "minority_required")
    private Boolean minorityRequired;

    @Column(name = "min_percentage")
    private Double minPercentage;

    @Column(name = "institution_type")
    private String institutionType; // e.g. "Government,Aided"

    @Column(name = "institution_location_type")
    private String institutionLocationType; // Rural, Urban

    @Column(name = "course_stream")
    private String courseStream;

    private String state;

    @Column(name = "disability_required")
    private Boolean disabilityRequired;

    @Column(name = "single_girl_child_required")
    private Boolean singleGirlChildRequired;

    @Column(name = "first_graduate_required")
    private Boolean firstGraduateRequired;

    @Column(name = "govt_school_only")
    private Boolean govtSchoolOnly;

    public SchemeEligibilityRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Scheme getScheme() { return scheme; }
    public void setScheme(Scheme scheme) { this.scheme = scheme; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }

    public Double getMaxIncome() { return maxIncome; }
    public void setMaxIncome(Double maxIncome) { this.maxIncome = maxIncome; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCommunityCategory() { return communityCategory; }
    public void setCommunityCategory(String communityCategory) { this.communityCategory = communityCategory; }

    public Boolean getMinorityRequired() { return minorityRequired; }
    public void setMinorityRequired(Boolean minorityRequired) { this.minorityRequired = minorityRequired; }

    public Double getMinPercentage() { return minPercentage; }
    public void setMinPercentage(Double minPercentage) { this.minPercentage = minPercentage; }

    public String getInstitutionType() { return institutionType; }
    public void setInstitutionType(String institutionType) { this.institutionType = institutionType; }

    public String getInstitutionLocationType() { return institutionLocationType; }
    public void setInstitutionLocationType(String institutionLocationType) { this.institutionLocationType = institutionLocationType; }

    public String getCourseStream() { return courseStream; }
    public void setCourseStream(String courseStream) { this.courseStream = courseStream; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Boolean getDisabilityRequired() { return disabilityRequired; }
    public void setDisabilityRequired(Boolean disabilityRequired) { this.disabilityRequired = disabilityRequired; }

    public Boolean getSingleGirlChildRequired() { return singleGirlChildRequired; }
    public void setSingleGirlChildRequired(Boolean singleGirlChildRequired) { this.singleGirlChildRequired = singleGirlChildRequired; }

    public Boolean getFirstGraduateRequired() { return firstGraduateRequired; }
    public void setFirstGraduateRequired(Boolean firstGraduateRequired) { this.firstGraduateRequired = firstGraduateRequired; }

    public Boolean getGovtSchoolOnly() { return govtSchoolOnly; }
    public void setGovtSchoolOnly(Boolean govtSchoolOnly) { this.govtSchoolOnly = govtSchoolOnly; }
}

