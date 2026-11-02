package com.scholarship.finder.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;
    private String state;
    private String district;

    @Column(name = "community_category")
    private String communityCategory;

    @Column(name = "religion_minority_flag")
    private Boolean religionMinorityFlag = false;

    @Column(name = "annual_family_income")
    private Double annualFamilyIncome;

    @Column(name = "disability_status")
    private Boolean disabilityStatus = false;

    @Column(name = "single_girl_child")
    private Boolean singleGirlChild = false;

    @Column(name = "first_graduate")
    private Boolean firstGraduate = false;

    @Column(name = "orphan_or_single_parent")
    private Boolean orphanOrSingleParent = false;

    private Boolean hosteller = false;

    @Column(name = "education_level", nullable = false)
    private String educationLevel;

    @Column(name = "current_class_or_year")
    private String currentClassOrYear;

    private String course;
    private String stream;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "institution_type")
    private String institutionType; // Government, Aided, Private

    @Column(name = "institution_location_type")
    private String institutionLocationType; // Rural, Urban

    @Column(name = "board_or_university")
    private String boardOrUniversity;

    @Column(name = "previous_exam_percentage")
    private Double previousExamPercentage;

    @Column(name = "entrance_exam_score")
    private Double entranceExamScore;

    @Column(name = "admission_type")
    private String admissionType; // Merit, Management, Quota

    public StudentProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

