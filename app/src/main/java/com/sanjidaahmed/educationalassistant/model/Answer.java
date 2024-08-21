package com.sanjidaahmed.educationalassistant.model;

public class Answer {
    private String question;
    private String answer;

    // Default constructor for deserialization
    public Answer() {
    }

    public Answer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
