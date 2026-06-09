package com.interview.service;

import com.interview.dto.AiEvaluationResponse;

public interface AiService {

	String detectSkills(
	        String resumeText,
	        String availableSkills);

    AiEvaluationResponse evaluateAnswer(
            String question,
            String masterAnswer,
            String candidateAnswer);

    String generateSummary(String transcript);
}