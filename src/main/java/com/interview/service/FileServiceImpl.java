package com.interview.service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.QuestionBank;
import com.interview.dto.QuestionItem;

import jakarta.annotation.PostConstruct;

@Service
public class FileServiceImpl implements FileService {

    

    private QuestionBank questionBank;
    
    private final Random random = new Random();

    @Override
    public QuestionItem getRandomQuestion(
            String skill,
            String complexity) {

        List<QuestionItem> questions =
                getQuestions(skill, complexity);

        if (questions.isEmpty()) {
            throw new RuntimeException("No Questions Found");
        }

        return questions.get(
                random.nextInt(questions.size()));
    }

    @Override
    public Set<String> getAvailableSkills() {

        return questionBank
                .getSkills()
                .keySet();
    }
    
    @Override
    public QuestionItem getNextUnusedQuestion(
            String skill,
            String complexity,
            List<String> usedQuestions) {

        List<QuestionItem> questions =
                getQuestions(skill, complexity);

        for (QuestionItem question : questions) {

            if (!usedQuestions.contains(
                    question.getQuestion())) {

                return question;
            }
        }

        return null;
    }
    
    @PostConstruct
    public void loadQuestionBank() {

        try {

            System.out.println("=================================");
            System.out.println("Loading Question Bank");

            Resource resource =
                    new ClassPathResource(
                            "questions.json");

            ObjectMapper mapper =
                    new ObjectMapper();

            questionBank =
                    mapper.readValue(
                            resource.getInputStream(),
                            QuestionBank.class);

            System.out.println(
                    "Question Bank Loaded Successfully");

        } catch (Exception e) {

            System.out.println(
                    "QUESTION BANK LOAD FAILED");

            e.printStackTrace();
        }
    }

    @Override
    public List<QuestionItem> getQuestions(
            String skill,
            String complexity) {

        if (questionBank == null) {

            System.out.println("Question Bank is NULL");

            return Collections.emptyList();
        }

        return questionBank.getSkills()
                .get(skill)
                .get(complexity);
    }
}