package org.Technical.dto;

import java.util.List;

public class SnagResponse {
    private String problem;
    private List<String> possibleCauses;
    private String firstCheck;
    private List<String> nextSteps;

    public SnagResponse() {
    }

    public SnagResponse(String problem, List<String> possibleCauses,
                        String firstCheck, List<String> nextSteps) {
        this.problem = problem;
        this.possibleCauses = possibleCauses;
        this.firstCheck = firstCheck;
        this.nextSteps = nextSteps;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public List<String> getPossibleCauses() {
        return possibleCauses;
    }

    public void setPossibleCauses(List<String> possibleCauses) {
        this.possibleCauses = possibleCauses;
    }

    public String getFirstCheck() {
        return firstCheck;
    }

    public void setFirstCheck(String firstCheck) {
        this.firstCheck = firstCheck;
    }

    public List<String> getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(List<String> nextSteps) {
        this.nextSteps = nextSteps;
    }
}
