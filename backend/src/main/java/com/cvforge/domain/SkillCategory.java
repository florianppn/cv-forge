package com.cvforge.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SkillCategory implements Serializable {
    private String categoryName;
    private List<String> skills = new ArrayList<>();

    public SkillCategory() {}

    public SkillCategory(String categoryName, List<String> skills) {
        this.categoryName = categoryName;
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String categoryName;
        private List<String> skills = new ArrayList<>();

        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder skills(List<String> skills) { this.skills = skills; return this; }
        public SkillCategory build() {
            return new SkillCategory(categoryName, skills);
        }
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
