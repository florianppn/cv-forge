package com.cvforge.dto.response;

import com.cvforge.domain.MatchResult;

public class MatchResponse {
    private MatchResult matchResult;

    public MatchResponse() {}

    public MatchResponse(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public MatchResult getMatchResult() { return matchResult; }
    public void setMatchResult(MatchResult matchResult) { this.matchResult = matchResult; }
}
