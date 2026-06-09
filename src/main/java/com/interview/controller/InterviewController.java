package com.interview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.interview.dto.AnswerRequest;
import com.interview.dto.InterviewResultResponse;
import com.interview.dto.InterviewStatusResponse;
import com.interview.dto.NextQuestionResponse;
import com.interview.dto.StartInterviewResponse;
import com.interview.service.InterviewService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

	@Autowired
	private InterviewService interviewService;
	
	@GetMapping("/status/{sessionId}")
	public InterviewStatusResponse getStatus(
	        @PathVariable Long sessionId) {

	    return interviewService
	            .getStatus(sessionId);
	}
	
	@GetMapping("/health")
	public String health() {
	    return "Interview Service Running";
	}

    @PostMapping("/start")
    public StartInterviewResponse startInterview(

            @RequestParam String name,

            @RequestParam String email,

            @RequestParam MultipartFile resume) {

        return interviewService.startInterview(
                name,
                email,
                resume);
    }

    @PostMapping("/answer")
    public NextQuestionResponse submitAnswer(
            @RequestBody AnswerRequest request) {

        return interviewService
                .submitAnswer(request);
    }

    @GetMapping("/results")
    public List<InterviewResultResponse>
    getAllResults() {

        return interviewService
                .getAllResults();
    }
    
    @GetMapping("/results/{sessionId}")
    public InterviewResultResponse getResult(
            @PathVariable Long sessionId) {

        return interviewService
                .getResult(sessionId);
    }
}