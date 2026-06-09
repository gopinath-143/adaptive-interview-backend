package com.interview.repository;

import com.interview.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository
        extends JpaRepository<InterviewSession, Long> {
}