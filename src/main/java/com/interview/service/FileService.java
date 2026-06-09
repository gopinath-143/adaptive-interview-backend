package com.interview.service;

import java.util.List;
import java.util.Set;

import com.interview.dto.QuestionItem;

public interface FileService {

    List<QuestionItem> getQuestions(
            String skill,
            String complexity);

    QuestionItem getRandomQuestion(
            String skill,
            String complexity);
    
    QuestionItem getNextUnusedQuestion(
            String skill,
            String complexity,
            List<String> usedQuestions);
    
    Set<String> getAvailableSkills();
}