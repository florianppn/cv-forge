package com.cvforge.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Experience implements Serializable {
    private String title;
    private String company;
    private String location;
    private String startDate;
    private String endDate;
    private boolean currentJob;
    private List<String> bullets = new ArrayList<>();

    public Experience() {}

    public Experience(String title, String company, String location, String startDate, String endDate, boolean currentJob, List<String> bullets) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentJob = currentJob;
        this.bullets = bullets != null ? bullets : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String company;
        private String location;
        private String startDate;
        private String endDate;
        private boolean currentJob;
        private List<String> bullets = new ArrayList<>();

        public Builder title(String title) { this.title = title; return this; }
        public Builder company(String company) { this.company = company; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder startDate(String startDate) { this.startDate = startDate; return this; }
        public Builder endDate(String endDate) { this.endDate = endDate; return this; }
        public Builder currentJob(boolean currentJob) { this.currentJob = currentJob; return this; }
        public Builder bullets(List<String> bullets) { this.bullets = bullets; return this; }
        public Experience build() {
            return new Experience(title, company, location, startDate, endDate, currentJob, bullets);
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isCurrentJob() { return currentJob; }
    public void setCurrentJob(boolean currentJob) { this.currentJob = currentJob; }

    public List<String> getBullets() { return bullets; }
    public void setBullets(List<String> bullets) { this.bullets = bullets; }
}
