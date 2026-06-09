package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Long questionId;

    @Column(length = 5000)
    private String candidateAnswer;

    @Column(length = 5000)
    private String masterAnswer;

    private Double aiScore;

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

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getCandidateAnswer() {
		return candidateAnswer;
	}

	public void setCandidateAnswer(String candidateAnswer) {
		this.candidateAnswer = candidateAnswer;
	}

	public String getMasterAnswer() {
		return masterAnswer;
	}

	public void setMasterAnswer(String masterAnswer) {
		this.masterAnswer = masterAnswer;
	}

	public Double getAiScore() {
		return aiScore;
	}

	public void setAiScore(Double aiScore) {
		this.aiScore = aiScore;
	}
    
    

}