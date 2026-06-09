package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity

public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private String candidateName;

    private String detectedSkill;

    private Double finalScore;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getDetectedSkill() {
		return detectedSkill;
	}

	public void setDetectedSkill(String detectedSkill) {
		this.detectedSkill = detectedSkill;
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

}