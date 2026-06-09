package com.interview.dto;


public class InterviewResultResponse {

    private Long sessionId;

    private String candidateName;

    private String email;

    private String skill;

    private Double finalScore;

    private String transcript;

    private String summary;

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public Double getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(Double finalScore) {
		this.finalScore = finalScore;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
    
    
}