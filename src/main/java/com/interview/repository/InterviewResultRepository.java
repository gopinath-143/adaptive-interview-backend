package com.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.entity.InterviewResult;

public interface InterviewResultRepository
        extends JpaRepository<InterviewResult, Long> {
	
	InterviewResult findBySessionId(Long sessionId);
	
}