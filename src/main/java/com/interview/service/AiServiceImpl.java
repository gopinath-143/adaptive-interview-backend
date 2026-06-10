package com.interview.service;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.interview.dto.AiEvaluationResponse;
import com.interview.dto.Message;
import com.interview.dto.OpenRouterRequest;
import com.interview.dto.OpenRouterResponse;
import com.interview.entity.AiAuditLog;
import com.interview.repository.AiAuditLogRepository;

@Service
public class AiServiceImpl implements AiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.url}")
    private String url;

    @Value("${openrouter.model}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private AiAuditLogRepository auditLogRepository;

    private String callAI(String prompt) {

        try {

            Message message =
                    new Message("user", prompt);

            OpenRouterRequest request =
                    new OpenRouterRequest();

            request.setModel(model);

            request.setMessages(
                    Arrays.asList(message));

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON);

            headers.setBearerAuth(apiKey);

            HttpEntity<OpenRouterRequest> entity =
                    new HttpEntity<>(
                            request,
                            headers);

            ResponseEntity<OpenRouterResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            OpenRouterResponse.class);

            if (response == null ||
                    response.getBody() == null) {

                System.out.println(
                        "OpenRouter returned empty response");

                return "";
            }

            OpenRouterResponse responseBody =
                    response.getBody();

            if (responseBody.getChoices() == null ||
                    responseBody.getChoices().isEmpty()) {

                System.out.println(
                        "No choices returned from OpenRouter");

                return "";
            }

            String aiResponse =
                    responseBody.getChoices()
                            .get(0)
                            .getMessage()
                            .getContent();

            // ============================
            // AI AUDIT LOGGING
            // ============================

            AiAuditLog auditLog =
                    new AiAuditLog();

            auditLog.setPrompt(prompt);

            auditLog.setResponse(aiResponse);

            auditLog.setCreatedAt(
                    LocalDateTime.now());

            auditLogRepository.save(auditLog);

            // ============================

            return aiResponse;

        } catch (Exception e) {

            System.out.println(
                    "OpenRouter API Error");

            e.printStackTrace();

            try {

                AiAuditLog auditLog =
                        new AiAuditLog();

                auditLog.setPrompt(prompt);

                auditLog.setResponse(
                        "ERROR : "
                                + e.getMessage());

                auditLog.setCreatedAt(
                        LocalDateTime.now());

                auditLogRepository.save(
                        auditLog);

            } catch (Exception ignored) {

            }

            return "";
        }
    }
    
    @Override
    public String detectSkills(
            String resumeText,
            String availableSkills) {

        String prompt =
                """
                You are an expert technical recruiter.

                Available Skills:

                %s

                Resume:

                %s

                Task:

                Identify ALL matching skills
                found in the resume.

                Return only comma separated skills.

                Example:

                Java,Spring Boot,MySQL,Git

                Do not explain.
                Do not add extra text.
                """
                .formatted(
                        availableSkills,
                        resumeText);

        return callAI(prompt);
    }

    @Override
    public AiEvaluationResponse evaluateAnswer(
            String question,
            String masterAnswer,
            String candidateAnswer) {

        AiEvaluationResponse response =
                new AiEvaluationResponse();

        // Handle skipped / blank answers

        if (candidateAnswer == null
                || candidateAnswer.trim().isEmpty()) {

            response.setScore(0.0);

            response.setFeedback(
                    "Question Skipped");

            return response;
        }

        String answer =
                candidateAnswer
                        .trim()
                        .toLowerCase();

        if (answer.equals("i don't know")
                || answer.equals("i dont know")
                || answer.equals("dont know")
                || answer.equals("don't know")
                || answer.equals("skip")
                || answer.equals("n/a")
                || answer.equals("no idea")) {

            response.setScore(0.0);

            response.setFeedback(
                    "Question Skipped");

            return response;
        }

        String prompt =
                """
                You are a strict technical interviewer.

                Question:
                %s

                Expected Answer:
                %s

                Candidate Answer:
                %s

                Scoring Rules:

                - Empty answer = 0
                - "I don't know" = 0
                - Completely wrong answer = 0 to 20
                - Partially correct answer = 21 to 60
                - Mostly correct answer = 61 to 85
                - Complete and technically correct answer = 86 to 100

                IMPORTANT:

                Return ONLY a numeric score.

                Example:
                78
                """
                .formatted(
                        question,
                        masterAnswer,
                        candidateAnswer);

        String aiResponse =
                callAI(prompt);

        System.out.println(
                "AI Response = "
                        + aiResponse);

        try {

            String scoreText =
                    aiResponse.trim()
                              .replaceAll(
                                      "[^0-9.]",
                                      "");

            double score =
                    Double.parseDouble(
                            scoreText);

            if (score < 0) {

                score = 0;
            }

            if (score > 100) {

                score = 100;
            }

            response.setScore(
                    score);

        } catch (Exception exception) {

            exception.printStackTrace();

            response.setScore(
                    0.0);
        }

        response.setFeedback(
                "AI Evaluation Completed");

        return response;
    }
    @Override
    public String generateSummary(String transcript) {

        String prompt =
                """
                You are a Senior Technical Interview Panel Lead.

                Analyze the interview transcript and provide a complete candidate evaluation report.

                Return the result in the EXACT format below.

                Candidate Interview Evaluation Report

                Skill Assessed: <Skill Name>

                Final Score: <0-100>

                Candidate Overall Review:
                Write 3-5 concise sentences summarizing the candidate's overall performance, technical understanding, confidence, communication skills, strengths, and weaknesses.

                Strengths:
                • Point 1
                • Point 2
                • Point 3

                Areas for Improvement:
                • Point 1
                • Point 2
                • Point 3

                Technical Assessment:
                • Core Concepts: Excellent/Good/Average/Poor
                • Problem Solving: Excellent/Good/Average/Poor
                • Communication: Excellent/Good/Average/Poor
                • Coding Knowledge: Excellent/Good/Average/Poor

                Interview Outcome:
                • Outstanding Performance
                • Good Performance
                • Average Performance
                • Below Expectations

                Manager Recommendation:
                Write 2-3 concise sentences explaining whether the candidate is suitable for the role and why.

                Selection Decision:
                • Strong Hire
                • Hire
                • Hold for Further Evaluation
                • Reject

                Confidence Level:
                • High
                • Medium
                • Low

                Suggested Learning Path:
                • Item 1
                • Item 2
                • Item 3

                Overall Verdict:
                Write 2-3 concise professional sentences summarizing whether the candidate should be selected and the key reasons behind the decision.

                IMPORTANT SCORING RULES:
                - Score >= 85 → Strong Hire
                - Score 70-84 → Hire
                - Score 50-69 → Hold for Further Evaluation
                - Score < 50 → Reject

                IMPORTANT:
                - Base the evaluation strictly on the transcript.
                - Be objective and professional.
                - Do not inflate scores.
                - Provide actionable feedback.
                - No markdown.
                - No ###.
                - No **.
                - No JSON.
                - Keep the report concise and structured.

                Transcript:

                %s
                """
                .formatted(transcript);

        return callAI(prompt);
    }

    }