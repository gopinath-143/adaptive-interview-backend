package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;

    private String email;

    @Column(length = 1000)
    private String detectedSkills;

    private Integer currentQuestionIndex;

    @Enumerated(EnumType.STRING)
    private ComplexityLevel currentComplexity;

    private Double runningScore;

    private Boolean completed;
    
    private Integer totalQuestions;
    
    private String skillCounts;
    
    public String getSkillCounts() {
		return skillCounts;
	}

	public void setSkillCounts(String skillCounts) {
		this.skillCounts = skillCounts;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	@Column(length = 5000)
    private String askedQuestions;
    

	public String getAskedQuestions() {
		return askedQuestions;
	}

	public void setAskedQuestions(String askedQuestions) {
		this.askedQuestions = askedQuestions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	

	public String getDetectedSkills() {
		return detectedSkills;
	}

	public void setDetectedSkills(String detectedSkills) {
		this.detectedSkills = detectedSkills;
	}

	public Integer getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}

	public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
		this.currentQuestionIndex = currentQuestionIndex;
	}

	public ComplexityLevel getCurrentComplexity() {
		return currentComplexity;
	}

	public void setCurrentComplexity(ComplexityLevel currentComplexity) {
		this.currentComplexity = currentComplexity;
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