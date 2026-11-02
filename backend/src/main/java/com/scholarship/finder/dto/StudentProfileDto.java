package com.scholarship.finder.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class StudentProfileDto {
    private Long id;
    private Long userId;
    private LocalDate dateOfBirth;
    private String gender;
    private String state;
    private String district;
    private String communityCategory;
    private Boolean religionMinorityFlag = false;
    private Double annualFamilyIncome;
    private Boolean disabilityStatus = false;
    private Boolean singleGirlChild = false;
    private Boolean firstGraduate = false;
    private Boolean orphanOrSingleParent = false;
    private Boolean hosteller = false;

    @NotBlank(message = "Education level is required")
    private String educationLevel;

    private String currentClassOrYear;
    private String course;
    private String stream;

    @NotBlank(message = "Institution name is required")
    private String institutionName;
    private String institutionType;
    private String institutionLocationType;
    private String boardOrUniversity;
    private Double previousExamPercentage;
    private Double entranceExamScore;
    private String admissionType;

    public StudentProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCommunityCategory() { return communityCategory; }
    public void setCommunityCategory(String communityCategory) { this.communityCategory = communityCategory; }

    public Boolean getReligionMinorityFlag() { return religionMinorityFlag; }
    public void setReligionMinorityFlag(Boolean religionMinorityFlag) { this.religionMinorityFlag = religionMinorityFlag; }

    public Double getAnnualFamilyIncome() { return annualFamilyIncome; }
    public void setAnnualFamilyIncome(Double annualFamilyIncome) { this.annualFamilyIncome = annualFamilyIncome; }

    public Boolean getDisabilityStatus() { return disabilityStatus; }
    public void setDisabilityStatus(Boolean disabilityStatus) { this.disabilityStatus = disabilityStatus; }

    public Boolean getSingleGirlChild() { return singleGirlChild; }
    public void setSingleGirlChild(Boolean singleGirlChild) { this.singleGirlChild = singleGirlChild; }

    public Boolean getFirstGraduate() { return firstGraduate; }
    public void setFirstGraduate(Boolean firstGraduate) { this.firstGraduate = firstGraduate; }

    public Boolean getOrphanOrSingleParent() { return orphanOrSingleParent; }
    public void setOrphanOrSingleParent(Boolean orphanOrSingleParent) { this.orphanOrSingleParent = orphanOrSingleParent; }

    public Boolean getHosteller() { return hosteller; }
    public void setHosteller(Boolean hosteller) { this.hosteller = hosteller; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getCurrentClassOrYear() { return currentClassOrYear; }
    public void setCurrentClassOrYear(String currentClassOrYear) { this.currentClassOrYear = currentClassOrYear; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getInstitutionType() { return institutionType; }
    public void setInstitutionType(String institutionType) { this.institutionType = institutionType; }

    public String getInstitutionLocationType() { return institutionLocationType; }
    public void setInstitutionLocationType(String institutionLocationType) { this.institutionLocationType = institutionLocationType; }

    public String getBoardOrUniversity() { return boardOrUniversity; }
    public void setBoardOrUniversity(String boardOrUniversity) { this.boardOrUniversity = boardOrUniversity; }

    public Double getPreviousExamPercentage() { return previousExamPercentage; }
    public void setPreviousExamPercentage(Double previousExamPercentage) { this.previousExamPercentage = previousExamPercentage; }

    public Double getEntranceExamScore() { return entranceExamScore; }
    public void setEntranceExamScore(Double entranceExamScore) { this.entranceExamScore = entranceExamScore; }

    public String getAdmissionType() { return admissionType; }
    public void setAdmissionType(String admissionType) { this.admissionType = admissionType; }
}

