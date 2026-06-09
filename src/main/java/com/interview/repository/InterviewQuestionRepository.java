package com.interview.repository;

import com.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQuestionRepository
        extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findBySessionId(Long sessionId);
    
    InterviewQuestion findTopBySessionIdOrderByIdDesc(
            Long sessionId);

}