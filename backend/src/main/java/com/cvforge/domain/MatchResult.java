package com.cvforge.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Serializable {
    private String targetJobTitle;
    private int matchScore;
    private List<String> matchedKeywords = new ArrayList<>();
    private List<String> missingKeywords = new ArrayList<>();
    private List<String> strengths = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();

    public MatchResult() {}

    public MatchResult(String targetJobTitle, int matchScore, List<String> matchedKeywords, List<String> missingKeywords, List<String> strengths, List<String> suggestions) {
        this.targetJobTitle = targetJobTitle;
        this.matchScore = matchScore;
        this.matchedKeywords = matchedKeywords != null ? matchedKeywords : new ArrayList<>();
        this.missingKeywords = missingKeywords != null ? missingKeywords : new ArrayList<>();
        this.strengths = strengths != null ? strengths : new ArrayList<>();
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String targetJobTitle;
        private int matchScore;
        private List<String> matchedKeywords = new ArrayList<>();
        private List<String> missingKeywords = new ArrayList<>();
        private List<String> strengths = new ArrayList<>();
        private List<String> suggestions = new ArrayList<>();

        public Builder targetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; return this; }
        public Builder matchScore(int matchScore) { this.matchScore = matchScore; return this; }
        public Builder matchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; return this; }
        public Builder missingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; return this; }
        public Builder strengths(List<String> strengths) { this.strengths = strengths; return this; }
        public Builder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }
        public MatchResult build() {
            return new MatchResult(targetJobTitle, matchScore, matchedKeywords, missingKeywords, strengths, suggestions);
        }
    }

    public String getTargetJobTitle() { return targetJobTitle; }
    public void setTargetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; }

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }

    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }

    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
