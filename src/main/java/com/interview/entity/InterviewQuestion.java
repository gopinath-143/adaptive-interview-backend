package com.interview.entity;

import jakarta.persistence.*;


@Entity

public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    @Column(length = 2000)
    private String questionText;
    
    @Column(length = 5000)
    private String masterAnswer;

    private String skill;

    @Enumerated(EnumType.STRING)
    private ComplexityLevel complexity;

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

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getMasterAnswer() {
		return masterAnswer;
	}

	public void setMasterAnswer(String masterAnswer) {
		this.masterAnswer = masterAnswer;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public ComplexityLevel getComplexity() {
		return complexity;
	}

	public void setComplexity(ComplexityLevel complexity) {
		this.complexity = complexity;
	}
    
    

}