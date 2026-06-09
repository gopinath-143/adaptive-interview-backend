package com.interview.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.interview.dto.AiEvaluationResponse;
import com.interview.dto.AnswerRequest;
import com.interview.dto.InterviewResultResponse;
import com.interview.dto.InterviewStatusResponse;
import com.interview.dto.NextQuestionResponse;
import com.interview.dto.QuestionItem;
import com.interview.dto.StartInterviewResponse;
import com.interview.entity.ComplexityLevel;
import com.interview.entity.InterviewAnswer;
import com.interview.entity.InterviewQuestion;
import com.interview.entity.InterviewResult;
import com.interview.entity.InterviewSession;
import com.interview.repository.InterviewAnswerRepository;
import com.interview.repository.InterviewQuestionRepository;
import com.interview.repository.InterviewResultRepository;
import com.interview.repository.InterviewSessionRepository;

@Service
public class InterviewServiceImpl implements InterviewService {

	private final ResumeParserService resumeParserService;
	private final FileService fileService;
	private final AiService aiService;

	private final InterviewSessionRepository sessionRepository;
	private final InterviewQuestionRepository questionRepository;
	private final InterviewAnswerRepository answerRepository;
	private final InterviewResultRepository resultRepository;

	@Value("${interview.questions.per.skill}")
	private int questionsPerSkill;

	public InterviewServiceImpl(ResumeParserService resumeParserService, FileService fileService, AiService aiService,
			InterviewSessionRepository sessionRepository, InterviewQuestionRepository questionRepository,
			InterviewAnswerRepository answerRepository, InterviewResultRepository resultRepository) {

		this.resumeParserService = resumeParserService;
		this.fileService = fileService;
		this.aiService = aiService;
		this.sessionRepository = sessionRepository;
		this.questionRepository = questionRepository;
		this.answerRepository = answerRepository;
		this.resultRepository = resultRepository;
	}

	@Override
	public StartInterviewResponse startInterview(String candidateName, String email, MultipartFile resume) {

		String resumeText = resumeParserService.extractText(resume);

		String availableSkills = String.join(",", fileService.getAvailableSkills());

		String skills = aiService.detectSkills(resumeText, availableSkills);
		List<String> validSkills = Arrays.stream(skills.split(",")).map(String::trim)
				.filter(skill -> fileService.getAvailableSkills().contains(skill)).toList();

		skills = String.join(",", validSkills);
		int totalQuestions =
		        validSkills.size()
		        * questionsPerSkill;

		System.out.println("Valid Skills: " + skills);
		System.out.println("Detected Skills: " + skills);

		String firstSkill = getRandomSkill(skills);
		
		

		List<QuestionItem> easyQuestions = fileService.getQuestions(firstSkill, "Easy");

		if (easyQuestions.isEmpty()) {

			throw new RuntimeException("No Questions Found");
		}

		QuestionItem firstQuestion = easyQuestions.get((int) (Math.random() * easyQuestions.size()));

		
		InterviewSession session = new InterviewSession();

		session.setCandidateName(candidateName);

		session.setEmail(email);

		session.setDetectedSkills(skills);
		session.setTotalQuestions(
		        totalQuestions);
		StringBuilder skillCountBuilder =
		        new StringBuilder();

		for (String skill : validSkills) {

		    skillCountBuilder
		            .append(skill)
		            .append("=0,");
		}

		session.setSkillCounts(
		        skillCountBuilder.toString());

		session.setCurrentQuestionIndex(1);

		session.setCurrentComplexity(ComplexityLevel.EASY);

		session.setRunningScore(0.0);

		session.setCompleted(false);

		session.setAskedQuestions(firstQuestion.getQuestion());

		session = sessionRepository.save(session);

		InterviewQuestion question = new InterviewQuestion();

		question.setSessionId(session.getId());

		question.setQuestionText(firstQuestion.getQuestion());

		question.setMasterAnswer(firstQuestion.getAnswer());

		question.setSkill(firstSkill);

		question.setComplexity(ComplexityLevel.EASY);

		questionRepository.save(question);
		
		incrementSkillCount(
		        session,
		        firstSkill);

		sessionRepository.save(
		        session);

		StartInterviewResponse response = new StartInterviewResponse();

		response.setSessionId(session.getId());

		response.setQuestion(firstQuestion.getQuestion());

		return response;
	}

	@Override
	public List<InterviewResultResponse> getAllResults() {

		return resultRepository.findAll().stream().map(result -> {

			InterviewResultResponse response = new InterviewResultResponse();

			response.setSessionId(result.getSessionId());

			response.setCandidateName(result.getCandidateName());

			response.setSkill(result.getDetectedSkill());

			response.setFinalScore(result.getFinalScore());

			response.setTranscript(result.getTranscript());

			response.setSummary(result.getSummary());

			return response;

		}).toList();
	}

	@Override
	public InterviewResultResponse getResult(Long sessionId) {

		InterviewResult result = resultRepository.findBySessionId(sessionId);

		if (result == null) {

			throw new RuntimeException("Interview Result Not Found");
		}

		InterviewResultResponse response = new InterviewResultResponse();

		response.setSessionId(result.getSessionId());

		response.setCandidateName(result.getCandidateName());

		response.setSkill(result.getDetectedSkill());

		response.setFinalScore(result.getFinalScore());

		response.setTranscript(result.getTranscript());

		response.setSummary(result.getSummary());

		return response;
	}

	@Override
	public NextQuestionResponse submitAnswer(AnswerRequest request) {

		InterviewSession session = sessionRepository.findById(request.getSessionId()).orElseThrow();

		InterviewQuestion currentQuestion = questionRepository.findTopBySessionIdOrderByIdDesc(session.getId());

		AiEvaluationResponse aiResult = aiService.evaluateAnswer(currentQuestion.getQuestionText(),
				currentQuestion.getMasterAnswer(), request.getAnswer());

		InterviewAnswer answer = new InterviewAnswer();

		answer.setSessionId(session.getId());
		answer.setQuestionId(currentQuestion.getId());
		answer.setCandidateAnswer(request.getAnswer());
		answer.setMasterAnswer(currentQuestion.getMasterAnswer());
		answer.setAiScore(aiResult.getScore());

		answerRepository.save(answer);
		
		

		double newScore = session.getRunningScore() + aiResult.getScore();

		session.setRunningScore(newScore);

		if (session.getCurrentQuestionIndex()
		        >= session.getTotalQuestions()) {

			completeInterview(session);

			NextQuestionResponse response = new NextQuestionResponse();

			response.setCompleted(true);
			response.setNextQuestion("Interview Completed Successfully");

			return response;
		}

		ComplexityLevel nextComplexity = determineComplexity(session.getCurrentComplexity(), aiResult.getScore());

		session.setCurrentComplexity(nextComplexity);

		session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);

		sessionRepository.save(session);

		List<String> usedQuestions = new ArrayList<>();

		if (session.getAskedQuestions() != null && !session.getAskedQuestions().isEmpty()) {

			usedQuestions = Arrays.asList(session.getAskedQuestions().split("\\|"));
		}

		String randomSkill =
		        getNextSkill(
		                session);

		System.out.println("Selected Skill = " + randomSkill);

		if (randomSkill == null) {

		    completeInterview(session);

		    NextQuestionResponse response =
		            new NextQuestionResponse();

		    response.setCompleted(true);

		    response.setNextQuestion(
		            "Interview Completed Successfully");

		    return response;
		}
		
		List<QuestionItem> questions = fileService.getQuestions(randomSkill,
				nextComplexity.name().substring(0, 1) + nextComplexity.name().substring(1).toLowerCase());

		QuestionItem nextQuestionItem = null;

		for (QuestionItem question : questions) {

			if (!usedQuestions.contains(question.getQuestion())) {

				nextQuestionItem = question;
				break;
			}
		}

		// If all questions are already used
		if (nextQuestionItem == null) {

			throw new RuntimeException("No more unused questions available for " + nextComplexity);
		}

		// Update askedQuestions in session

		String existingQuestions = session.getAskedQuestions();

		if (existingQuestions == null) {
			existingQuestions = "";
		}

		session.setAskedQuestions(existingQuestions + "|" + nextQuestionItem.getQuestion());

		sessionRepository.save(session);

		// Save question in DB

		InterviewQuestion nextQuestion = new InterviewQuestion();

		nextQuestion.setSessionId(session.getId());

		nextQuestion.setQuestionText(nextQuestionItem.getQuestion());

		nextQuestion.setMasterAnswer(nextQuestionItem.getAnswer());

		nextQuestion.setSkill(randomSkill);

		nextQuestion.setComplexity(nextComplexity);

		questionRepository.save(nextQuestion);
		
		incrementSkillCount(
		        session,
		        randomSkill);

		sessionRepository.save(
		        session);

		// Response

		NextQuestionResponse response = new NextQuestionResponse();

		response.setCompleted(false);

		response.setNextQuestion(nextQuestionItem.getQuestion());

		return response;
	}

	private ComplexityLevel determineComplexity(ComplexityLevel current, Double score) {

		if (score <= 60) {
			return current;
		}

		if (current == ComplexityLevel.EASY) {
			return ComplexityLevel.MEDIUM;
		}

		if (current == ComplexityLevel.MEDIUM) {
			return ComplexityLevel.HARD;
		}

		return ComplexityLevel.HARD;
	}

	private QuestionItem getNextQuestion(String skill, ComplexityLevel complexity) {

		List<QuestionItem> questions = fileService.getQuestions(skill,
				complexity.name().substring(0, 1) + complexity.name().substring(1).toLowerCase());

		return questions.get(0);
	}

	private void completeInterview(InterviewSession session) {

		List<InterviewQuestion> questions = questionRepository.findBySessionId(session.getId());

		List<InterviewAnswer> answers = answerRepository.findBySessionId(session.getId());

		StringBuilder transcript = new StringBuilder();

		for (int i = 0; i < questions.size(); i++) {

			transcript.append("Question : ").append(questions.get(i).getQuestionText()).append("\n");

			if (i < answers.size()) {

				transcript.append("Answer : ").append(answers.get(i).getCandidateAnswer()).append("\n");

				transcript.append("Score : ").append(answers.get(i).getAiScore()).append("\n\n");
			}
		}

		String summary = aiService.generateSummary(transcript.toString());

		InterviewResult result = new InterviewResult();

		result.setSessionId(session.getId());

		result.setCandidateName(session.getCandidateName());

		result.setDetectedSkill(session.getDetectedSkills());

		result.setFinalScore(
		        session.getRunningScore()
		                / session.getTotalQuestions());

		result.setTranscript(transcript.toString());

		result.setSummary(summary);

		resultRepository.save(result);

		session.setCompleted(true);

		sessionRepository.save(session);
	}

	private String getRandomSkill(String detectedSkills) {

		List<String> skills = Arrays.stream(detectedSkills.split(",")).map(String::trim).toList();

		Random random = new Random();

		return skills.get(random.nextInt(skills.size()));
	}

	@Override
	public InterviewStatusResponse getStatus(Long sessionId) {

		InterviewSession session = sessionRepository.findById(sessionId).orElseThrow();

		InterviewStatusResponse response = new InterviewStatusResponse();

		response.setSessionId(session.getId());

		response.setCurrentQuestion(session.getCurrentQuestionIndex());

		response.setTotalQuestions(
		        session.getTotalQuestions());

		response.setRunningScore(session.getRunningScore());

		response.setCompleted(session.getCompleted());

		return response;
	}
	private String getNextSkill(
	        InterviewSession session) {

	    List<String> availableSkills =
	            new ArrayList<>();

	    String[] entries =
	            session.getSkillCounts()
	                    .split(",");

	    for (String entry : entries) {

	        if (entry == null ||
	                entry.isBlank()) {

	            continue;
	        }

	        String[] parts =
	                entry.split("=");

	        String skill =
	                parts[0];

	        int count =
	                Integer.parseInt(
	                        parts[1]);

	        if (count < questionsPerSkill) {

	            availableSkills.add(
	                    skill);
	        }
	    }

	    if (availableSkills.isEmpty()) {

	        return null;
	    }

	    Random random =
	            new Random();

	    return availableSkills.get(
	            random.nextInt(
	                    availableSkills.size()));
	}
	private void incrementSkillCount(
	        InterviewSession session,
	        String skill) {

	    StringBuilder updated =
	            new StringBuilder();

	    String[] entries =
	            session.getSkillCounts()
	                    .split(",");

	    for (String entry : entries) {

	        if (entry == null ||
	                entry.isBlank()) {

	            continue;
	        }

	        String[] parts =
	                entry.split("=");

	        String currentSkill =
	                parts[0];

	        int count =
	                Integer.parseInt(
	                        parts[1]);

	        if (currentSkill.equals(
	                skill)) {

	            count++;
	        }

	        updated.append(
	                currentSkill)
	                .append("=")
	                .append(count)
	                .append(",");
	    }

	    session.setSkillCounts(
	            updated.toString());
	}
}