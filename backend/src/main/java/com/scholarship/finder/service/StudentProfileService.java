package com.scholarship.finder.service;

import com.scholarship.finder.dto.StudentProfileDto;
import com.scholarship.finder.entity.StudentProfile;
import com.scholarship.finder.entity.User;
import com.scholarship.finder.exception.ResourceNotFoundException;
import com.scholarship.finder.repository.StudentProfileRepository;
import com.scholarship.finder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public StudentProfileService(StudentProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public StudentProfileDto getProfileByUserId(Long userId) {
        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));
        return toDto(profile);
    }

    public boolean hasProfile(Long userId) {
        return profileRepository.findByUserId(userId).isPresent();
    }

    @Transactional
    public StudentProfileDto createOrUpdateProfile(Long userId, StudentProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        StudentProfile profile = profileRepository.findByUserId(userId).orElse(new StudentProfile());
        profile.setUser(user);

        // General Demographic Information
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setGender(dto.getGender());
        profile.setState(dto.getState() != null ? dto.getState() : "Tamil Nadu");
        profile.setDistrict(dto.getDistrict());
        profile.setCommunityCategory(dto.getCommunityCategory());
        profile.setReligionMinorityFlag(dto.getReligionMinorityFlag() != null ? dto.getReligionMinorityFlag() : false);
        profile.setAnnualFamilyIncome(dto.getAnnualFamilyIncome());
        profile.setDisabilityStatus(dto.getDisabilityStatus() != null ? dto.getDisabilityStatus() : false);
        profile.setSingleGirlChild(dto.getSingleGirlChild() != null ? dto.getSingleGirlChild() : false);
        profile.setFirstGraduate(dto.getFirstGraduate() != null ? dto.getFirstGraduate() : false);
        profile.setOrphanOrSingleParent(dto.getOrphanOrSingleParent() != null ? dto.getOrphanOrSingleParent() : false);
        profile.setHosteller(dto.getHosteller() != null ? dto.getHosteller() : false);

        // Educational Information
        profile.setEducationLevel(dto.getEducationLevel());
        profile.setCurrentClassOrYear(dto.getCurrentClassOrYear());
        profile.setCourse(dto.getCourse());
        profile.setStream(dto.getStream());
        profile.setInstitutionName(dto.getInstitutionName());
        profile.setInstitutionType(dto.getInstitutionType());
        profile.setInstitutionLocationType(dto.getInstitutionLocationType());
        profile.setBoardOrUniversity(dto.getBoardOrUniversity());
        profile.setPreviousExamPercentage(dto.getPreviousExamPercentage());
        profile.setEntranceExamScore(dto.getEntranceExamScore());
        profile.setAdmissionType(dto.getAdmissionType());

        StudentProfile saved = profileRepository.save(profile);
        return toDto(saved);
    }

    public StudentProfileDto toDto(StudentProfile entity) {
        StudentProfileDto dto = new StudentProfileDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());
        dto.setState(entity.getState());
        dto.setDistrict(entity.getDistrict());
        dto.setCommunityCategory(entity.getCommunityCategory());
        dto.setReligionMinorityFlag(entity.getReligionMinorityFlag());
        dto.setAnnualFamilyIncome(entity.getAnnualFamilyIncome());
        dto.setDisabilityStatus(entity.getDisabilityStatus());
        dto.setSingleGirlChild(entity.getSingleGirlChild());
        dto.setFirstGraduate(entity.getFirstGraduate());
        dto.setOrphanOrSingleParent(entity.getOrphanOrSingleParent());
        dto.setHosteller(entity.getHosteller());
        dto.setEducationLevel(entity.getEducationLevel());
        dto.setCurrentClassOrYear(entity.getCurrentClassOrYear());
        dto.setCourse(entity.getCourse());
        dto.setStream(entity.getStream());
        dto.setInstitutionName(entity.getInstitutionName());
        dto.setInstitutionType(entity.getInstitutionType());
        dto.setInstitutionLocationType(entity.getInstitutionLocationType());
        dto.setBoardOrUniversity(entity.getBoardOrUniversity());
        dto.setPreviousExamPercentage(entity.getPreviousExamPercentage());
        dto.setEntranceExamScore(entity.getEntranceExamScore());
        dto.setAdmissionType(entity.getAdmissionType());
        return dto;
    }
}

