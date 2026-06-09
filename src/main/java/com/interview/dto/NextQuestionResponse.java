package com.interview.dto;

public class NextQuestionResponse {

    private boolean completed;

    private String nextQuestion;

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getNextQuestion() {
		return nextQuestion;
	}

	public void setNextQuestion(String nextQuestion) {
		this.nextQuestion = nextQuestion;
	}
    
    

}