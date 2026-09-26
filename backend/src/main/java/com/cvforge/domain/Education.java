package com.cvforge.domain;

import java.io.Serializable;

public class Education implements Serializable {
    private String degree;
    private String institution;
    private String location;
    private String startDate;
    private String graduationDate;
    private String details;

    public Education() {}

    public Education(String degree, String institution, String location, String startDate, String graduationDate, String details) {
        this.degree = degree;
        this.institution = institution;
        this.location = location;
        this.startDate = startDate;
        this.graduationDate = graduationDate;
        this.details = details;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String degree;
        private String institution;
        private String location;
        private String startDate;
        private String graduationDate;
        private String details;

        public Builder degree(String degree) { this.degree = degree; return this; }
        public Builder institution(String institution) { this.institution = institution; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder startDate(String startDate) { this.startDate = startDate; return this; }
        public Builder graduationDate(String graduationDate) { this.graduationDate = graduationDate; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Education build() {
            return new Education(degree, institution, location, startDate, graduationDate, details);
        }
    }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getGraduationDate() { return graduationDate; }
    public void setGraduationDate(String graduationDate) { this.graduationDate = graduationDate; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
