package com.cvforge.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CvContent implements Serializable {
    private ContactInfo contactInfo;
    private String professionalSummary;
    private List<Experience> experiences = new ArrayList<>();
    private List<Education> educations = new ArrayList<>();
    private List<SkillCategory> skills = new ArrayList<>();
    private MatchResult matchResult;

    public CvContent() {}

    public CvContent(ContactInfo contactInfo, String professionalSummary, List<Experience> experiences, List<Education> educations, List<SkillCategory> skills, MatchResult matchResult) {
        this.contactInfo = contactInfo;
        this.professionalSummary = professionalSummary;
        this.experiences = experiences != null ? experiences : new ArrayList<>();
        this.educations = educations != null ? educations : new ArrayList<>();
        this.skills = skills != null ? skills : new ArrayList<>();
        this.matchResult = matchResult;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContactInfo contactInfo;
        private String professionalSummary;
        private List<Experience> experiences = new ArrayList<>();
        private List<Education> educations = new ArrayList<>();
        private List<SkillCategory> skills = new ArrayList<>();
        private MatchResult matchResult;

        public Builder contactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; return this; }
        public Builder professionalSummary(String professionalSummary) { this.professionalSummary = professionalSummary; return this; }
        public Builder experiences(List<Experience> experiences) { this.experiences = experiences; return this; }
        public Builder educations(List<Education> educations) { this.educations = educations; return this; }
        public Builder skills(List<SkillCategory> skills) { this.skills = skills; return this; }
        public Builder matchResult(MatchResult matchResult) { this.matchResult = matchResult; return this; }
        public CvContent build() {
            return new CvContent(contactInfo, professionalSummary, experiences, educations, skills, matchResult);
        }
    }

    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }

    public String getProfessionalSummary() { return professionalSummary; }
    public void setProfessionalSummary(String professionalSummary) { this.professionalSummary = professionalSummary; }

    public List<Experience> getExperiences() { return experiences; }
    public void setExperiences(List<Experience> experiences) { this.experiences = experiences; }

    public List<Education> getEducations() { return educations; }
    public void setEducations(List<Education> educations) { this.educations = educations; }

    public List<SkillCategory> getSkills() { return skills; }
    public void setSkills(List<SkillCategory> skills) { this.skills = skills; }

    public MatchResult getMatchResult() { return matchResult; }
    public void setMatchResult(MatchResult matchResult) { this.matchResult = matchResult; }
}
