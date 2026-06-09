package com.interview.dto;

public class InterviewStatusResponse {

    private Long sessionId;

    private Integer currentQuestion;

    private Integer totalQuestions;

    private Double runningScore;

    private Boolean completed;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Integer currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Double getRunningScore() {
        return runningScore;
    }

    public void setRunningScore(Double runningScore) {
        this.runningScore = runningScore;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}