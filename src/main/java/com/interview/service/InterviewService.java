package com.interview.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.interview.dto.AnswerRequest;
import com.interview.dto.InterviewResultResponse;
import com.interview.dto.InterviewStatusResponse;
import com.interview.dto.NextQuestionResponse;
import com.interview.dto.StartInterviewResponse;

public interface InterviewService {

    StartInterviewResponse startInterview(
            String candidateName,
            String email,
            MultipartFile resume);

    NextQuestionResponse submitAnswer(
            AnswerRequest request);

    List<InterviewResultResponse> getAllResults();
    
    InterviewResultResponse getResult(
            Long sessionId);
    
    InterviewStatusResponse getStatus(
            Long sessionId);
}